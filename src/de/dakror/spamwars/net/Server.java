package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.World;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet02Reject.Cause;
import de.dakror.spamwars.net.packet.Packet03Attribute;
import de.dakror.spamwars.net.packet.Packet04ServerInfo;
import de.dakror.spamwars.net.packet.Packet05World;
import de.dakror.spamwars.net.packet.Packet06PlayerData;
import de.dakror.spamwars.net.packet.Packet07Animation;
import de.dakror.spamwars.net.packet.Packet08Projectile;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int MAX_PLAYERS = 4;
	public static final int PORT = 19950;
	public static final int PACKETSIZE = 255; // bytes
	
	public static final String MAP_FILE = "/map/map2.txt";
	
	public boolean running;
	
	boolean lobby;
	
	DatagramSocket socket;
	World world;
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	int x = 140;
	int y = 500;
	
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
		world = new World(getClass().getResource(MAP_FILE));
		try
		{
			for (User u : clients)
				world.addEntity(new Player(x, y, u));
			
			sendPacketToAllClients(new Packet03Attribute("pos", x + "," + y));
			
			sendPacketToAllClients(new Packet05World(world));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addLateJoiner(User user)
	{
		world.addEntity(new Player(x, y, user));
		try
		{
			sendPacket(new Packet03Attribute("pos", x + "," + y), user);
			sendPacket(new Packet05World(world), user);
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
						return;
					}
					catch (Exception e)
					{}
				}
				// else if (clients.size() == MAX_PLAYERS)
				// {
				// try
				// {
				// CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): game full");
				// sendPacket(new Packet02Reject(Cause.FULL), user);
				// return;
				// }
				// catch (Exception e)
				// {}
				// }
				for (User p : clients)
				{
					if (p.getUsername().equals(packet.getUsername()))
					{
						try
						{
							sendPacket(new Packet02Reject(Cause.USERNAMETAKEN), user);
							CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): username taken");
							return;
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
					sendPacketToAllClients(new Packet04ServerInfo(clients.toArray(new User[] {})));
				}
				catch (Exception e)
				{}
				if (!lobby) addLateJoiner(user);
				break;
			}
			case DISCONNECT:
			{
				Packet01Disconnect p = new Packet01Disconnect(data);
				
				for (User u : clients)
				{
					if (u.getUsername().equals(p.getUsername()))
					{
						clients.remove(u);
						break;
					}
				}
				
				try
				{
					sendPacketToAllClients(p);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				break;
			}
			case SERVERINFO:
			{
				User user = new User(null, address, port);
				try
				{
					sendPacket(new Packet04ServerInfo(clients.toArray(new User[] {})), user);
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
				
				if (Game.world == null) break;
				
				for (Entity e : world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getIP().equals(address) && ((Player) e).getUser().getPort() == port)
					{
						e.setPos(p.getPosition());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						e.setLife(p.getLife());
						((Player) e).setStyle(p.getStyle());
						if (((Player) e).getWeapon().type != p.getWeaponType()) ((Player) e).setWeapon(p.getWeaponType());
						
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
					if (u.getUsername().equals(p.getKiller())) u.K++;
					if (u.getUsername().equals(p.getDead())) u.D++;
				}
				
				try
				{
					sendPacketToAllClientsExceptOne(p, new User(p.getDead(), address, port));
					sendPacketToAllClients(new Packet04ServerInfo(clients.toArray(new User[] {})));
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
			if (!u.getIP().equals(exception.getIP()) && u.getPort() != exception.getPort()) sendPacket(p, u);
	}
	
	public void sendPacket(Packet p, User u) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
	}
	
	public void shutdown()
	{
		running = false;
		try
		{
			sendPacketToAllClients(new Packet01Disconnect("##", de.dakror.spamwars.net.packet.Packet01Disconnect.Cause.SERVER_CLOSED));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		socket.close();
		CFG.p("[SERVER]: Server closed");
	}
}
