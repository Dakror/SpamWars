package de.dakror.spamwars.net;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.ServerUpdater;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.game.world.World;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet02Reject.Cause;
import de.dakror.spamwars.net.packet.Packet03Attribute;
import de.dakror.spamwars.net.packet.Packet04PlayerList;
import de.dakror.spamwars.net.packet.Packet05Chunk;
import de.dakror.spamwars.net.packet.Packet06PlayerData;
import de.dakror.spamwars.net.packet.Packet07Animation;
import de.dakror.spamwars.net.packet.Packet08Projectile;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.net.packet.Packet11GameInfo;
import de.dakror.spamwars.net.packet.Packet11GameInfo.GameMode;
import de.dakror.spamwars.net.packet.Packet12Stomp;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int MAX_PLAYERS = 10;
	public static final int PORT = 19950;
	public static final int PACKETSIZE = 255; // bytes
	
	public boolean running;
	
	public File map = new File(CFG.DIR, "maps/BigSnowSurfaceStoneBasementDefault.map");
	
	public boolean lobby;
	public long gameStarted;
	
	DatagramSocket socket;
	public World world;
	public ServerUpdater updater;
	public int minutes;
	public GameMode mode;
	
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	public Server(InetAddress ip)
	{
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(ip, Server.PORT));
			setName("Server-Thread");
			setPriority(MAX_PRIORITY);
			CFG.p("[SERVER]: Starting server at " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			lobby = true;
			start();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data, packet.getAddress(), packet.getPort());
			}
			catch (SocketException e)
			{}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void startGame()
	{
		lobby = false;
		world = new World(Helper.getFileContent(map));
		world.render(mode);
		world.render.flush();
		
		gameStarted = System.currentTimeMillis();
		
		updater = new ServerUpdater();
		try
		{
			final HashMap<Vector, Integer> spots = new HashMap<>();
			for (Vector v : world.spawns)
				spots.put(v, 0);
			
			sendPacketToAllClients(new Packet11GameInfo(minutes, mode));
			
			for (User u : clients)
			{
				ArrayList<Vector> vals = new ArrayList<>(spots.keySet());
				Collections.sort(vals, new Comparator<Vector>()
				{
					@Override
					public int compare(Vector o1, Vector o2)
					{
						return Integer.compare(spots.get(o1), spots.get(o1));
					}
				});
				
				int x = (int) (vals.get(0).x * Tile.SIZE);
				int y = (int) (vals.get(0).y * Tile.SIZE);
				
				spots.put(vals.get(0), spots.get(vals.get(0)) + 1);
				
				world.addEntity(new Player(x, y, u));
				sendPacketToAllClients(new Packet03Attribute("pos", x + "," + y));
			}
			
			sendWorld(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendWorld(User user)
	{
		try
		{
			for (int i = 0; i < Math.ceil((world.width / Tile.SIZE) / (float) Packet05Chunk.SIZE); i++)
			{
				for (int j = 0; j < Math.ceil((world.height / Tile.SIZE) / (float) Packet05Chunk.SIZE); j++)
				{
					if (user == null) sendPacketToAllClients(new Packet05Chunk(world, new Point(i, j)));
					else sendPacket(new Packet05Chunk(world, new Point(i, j)), user);
				}
			}
			
			if (user == null) sendPacketToAllClients(new Packet03Attribute("worldsize", world.width + "_" + world.height));
			else sendPacket(new Packet03Attribute("worldsize", world.width + "_" + world.height), user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addLateJoiner(User user)
	{
		Vector v = world.getBestSpawnPoint();
		world.addEntity(new Player(v.x * Tile.SIZE, v.x * Tile.SIZE, user));
		try
		{
			sendPacket(new Packet11GameInfo(minutes, mode), user);
			sendPacket(new Packet03Attribute("pos", (int) (v.x * Tile.SIZE) + "," + (int) (v.x * Tile.SIZE)), user);
			sendWorld(user);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		switch (type)
		{
			case INVALID:
			case ENTITYSTATUS:
			{
				break;
			}
			case CONNECT:
			{
				Packet00Connect packet = new Packet00Connect(data);
				User user = new User(packet.getUsername(), address, port);
				if (packet.getVersion() < CFG.VERSION)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): outdated client");
						sendPacket(new Packet02Reject(Cause.OUTDATEDCLIENT), user);
						return;
					}
					catch (Exception e)
					{}
				}
				else if (packet.getVersion() > CFG.VERSION)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): outdated server");
						sendPacket(new Packet02Reject(Cause.OUTDATEDSERVER), user);
						break;
					}
					catch (Exception e)
					{}
				}
				else if (clients.size() == MAX_PLAYERS)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): game full");
						sendPacket(new Packet02Reject(Cause.FULL), user);
						break;
					}
					catch (Exception e)
					{}
				}
				for (User p : clients)
				{
					if (p.getUsername().equals(packet.getUsername()))
					{
						try
						{
							sendPacket(new Packet02Reject(Cause.USERNAMETAKEN), user);
							CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): username taken");
							break;
						}
						catch (Exception e)
						{}
					}
				}
				CFG.p("[SERVER]: " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + ") has connected.");
				user.setPort(port);
				clients.add(user);
				try
				{
					sendPacket(new Packet03Attribute("user", user.serialize()), user);
					sendPacketToAllClients(packet);
					sendPacketToAllClients(new Packet04PlayerList(clients.toArray(new User[] {})));
				}
				catch (Exception e)
				{}
				if (!lobby) addLateJoiner(user);
				break;
			}
			case DISCONNECT:
			{
				Packet01Disconnect p = new Packet01Disconnect(data);
				
				try
				{
					sendPacketToAllClients(p);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				for (User u : clients)
				{
					if (u.getUsername().equals(p.getUsername()))
					{
						CFG.p("[SERVER]: " + u.getUsername() + " (" + address.getHostAddress() + ":" + port + ") left the game.");
						clients.remove(u);
						break;
					}
				}
				
				break;
			}
			case SERVERINFO:
			{
				User user = new User(null, address, port);
				try
				{
					sendPacket(new Packet04PlayerList(clients.toArray(new User[] {})), user);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			}
			case PLAYER:
			{
				Packet06PlayerData p = new Packet06PlayerData(data);
				User user = null;
				
				if (world == null) break;
				
				for (Entity e : world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getUsername().equals(p.getUsername()))
					{
						e.setPos(p.getPosition());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						e.setLife(p.getLife());
						
						((Player) e).setStyle(p.getStyle());
						if (((Player) e).getWeapon().type != p.getWeaponType()) ((Player) e).setWeapon(p.getWeaponType());
						
						((Player) e).getWeapon().ammo = p.getAmmo();
						((Player) e).getWeapon().capacity = p.getCapacity();
						((Player) e).getWeapon().rot2 = p.getRot();
						
						user = ((Player) e).getUser();
						break;
					}
				}
				
				try
				{
					sendPacketToAllClientsExceptOne(p, user);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				break;
			}
			case ANIMATION:
			{
				try
				{
					sendPacketToAllClientsExceptOne(new Packet07Animation(data), new User(null, address, port));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				break;
			}
			case PROJECTILE:
			{
				Packet08Projectile p = new Packet08Projectile(data);
				
				if (world == null) break;
				
				world.addProjectile(p.getProjectile(), false);
				
				try
				{
					sendPacketToAllClientsExceptOne(new Packet08Projectile(data), new User(null, address, port));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				break;
			}
			case KILL:
			{
				Packet09Kill p = new Packet09Kill(data);
				
				for (User u : clients)
				{
					if (u.getUsername().equals(p.getKiller()) && !p.getDead().equals(p.getKiller())) u.K++;
					if (u.getUsername().equals(p.getDead())) u.D++;
				}
				
				try
				{
					sendPacketToAllClients(p);
					sendPacketToAllClients(new Packet04PlayerList(clients.toArray(new User[] {})));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			case STOMP:
			{
				Packet12Stomp p = new Packet12Stomp(data);
				
				try
				{
					for (User u : clients)
					{
						if (u.getUsername().equals(p.getStomped()))
						{
							sendPacket(p, u);
							break;
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				break;
			}
			default:
				CFG.p("[SERVER]: reveived unhandled packet (" + address.getHostAddress() + ":" + port + ") " + type + " [" + Packet.readData(data) + "]");
		}
	}
	
	public void sendPacketToAllClients(Packet p) throws Exception
	{
		for (User u : clients)
			sendPacket(p, u);
	}
	
	public void sendPacketToAllClientsExceptOne(Packet p, User exception) throws Exception
	{
		for (User u : clients)
		{
			if (exception.getUsername() == null)
			{
				if (exception.getIP().equals(u.getIP()) && exception.getPort() == u.getPort()) continue;
			}
			else if (exception.getUsername().equals(u.getUsername())) continue;
			sendPacket(p, u);
		}
	}
	
	public void sendPacket(Packet p, User u) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
	}
	
	public void shutdown()
	{
		try
		{
			sendPacketToAllClients(new Packet01Disconnect("##", de.dakror.spamwars.net.packet.Packet01Disconnect.Cause.SERVER_CLOSED));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		running = false;
		if (updater != null) updater.closeRequested = true;
		socket.close();
		CFG.p("[SERVER]: Server closed");
	}
}
