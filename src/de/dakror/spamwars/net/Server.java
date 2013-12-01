package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.World;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet0Connect;
import de.dakror.spamwars.net.packet.Packet1Reject;
import de.dakror.spamwars.net.packet.Packet1Reject.Cause;
import de.dakror.spamwars.net.packet.Packet2Attribute;
import de.dakror.spamwars.net.packet.Packet3ServerInfo;
import de.dakror.spamwars.net.packet.Packet4World;
import de.dakror.spamwars.net.packet.Packet5PlayerData;
import de.dakror.spamwars.net.packet.Packet6Animation;
import de.dakror.spamwars.net.packet.Packet7Projectile;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public static final int MAX_PLAYERS = 4;
	public static final int PORT = 19950;
	public static final int PACKETSIZE = 255; // bytes
	
	public static final String MAP_FILE = "/map/map.txt";
	
	public boolean running;
	
	boolean lobby;
	
	DatagramSocket socket;
	World world;
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
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		shutdown();
	}
	
	public void startGame()
	{
		lobby = false;
		world = new World(getClass().getResource(MAP_FILE));
		try
		{
			int x = 140;
			int y = 500;
			
			for (User u : clients)
				world.addEntity(new Player(x, y, u), false);
			
			sendPacketToAllClients(new Packet2Attribute("pos", x + "," + y));
			
			sendPacketToAllClients(new Packet4World(world));
		}
		catch (Exception e)
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
				Packet0Connect packet = new Packet0Connect(data);
				User user = new User(packet.getUsername(), address, port);
				if (packet.getVersion() < CFG.VERSION)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): outdated client");
						sendPacket(new Packet1Reject(Cause.OUTDATEDCLIENT), user);
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
						sendPacket(new Packet1Reject(Cause.OUTDATEDSERVER), user);
						return;
					}
					catch (Exception e)
					{}
				}
				else if (!lobby)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): game already started");
						sendPacket(new Packet1Reject(Cause.GAMERUNNING), user);
						return;
					}
					catch (Exception e)
					{}
				}
				else if (clients.size() == MAX_PLAYERS)
				{
					try
					{
						CFG.p("[SERVER]: Rejected " + packet.getUsername() + " (" + address.getHostAddress() + ":" + port + "): game full");
						sendPacket(new Packet1Reject(Cause.FULL), user);
						return;
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
							sendPacket(new Packet1Reject(Cause.USERNAMETAKEN), user);
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
					sendPacketToAllClients(packet);
					sendPacketToAllClients(new Packet3ServerInfo(clients.toArray(new User[] {})));
				}
				catch (Exception e)
				{}
				break;
			}
			case SERVERINFO:
			{
				User user = new User(null, address, port);
				try
				{
					sendPacket(new Packet3ServerInfo(clients.toArray(new User[] {})), user);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				break;
			}
			case PLAYER:
			{
				Packet5PlayerData p = new Packet5PlayerData(data);
				User user = null;
				for (Entity e : world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getIP().equals(address))
					{
						e.setPos(p.getPosition());
						// e.setVelocity(p.getVelocity());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						e.setLife(p.getLife());
						((Player) e).setStyle(p.getStyle());
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
					sendPacketToAllClientsExceptOne(new Packet6Animation(data), new User(null, address, port));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				break;
			}
			case PROJECTILE:
			{
				Packet7Projectile p = new Packet7Projectile(data);
				world.addProjectile(p.getProjectile(), false);
				
				try
				{
					sendPacketToAllClientsExceptOne(new Packet7Projectile(data), new User(null, address, port));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
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
	{}
}
