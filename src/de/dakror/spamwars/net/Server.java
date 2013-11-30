package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet0Connect;
import de.dakror.spamwars.net.packet.Packet1Reject;
import de.dakror.spamwars.net.packet.Packet1Reject.Cause;
import de.dakror.spamwars.net.packet.Packet3ServerInfo;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Server extends Thread
{
	public boolean running;
	
	boolean lobby;
	public static final int MAX_PLAYERS = 4;
	
	DatagramSocket socket;
	public CopyOnWriteArrayList<User> clients = new CopyOnWriteArrayList<>();
	
	public Server(InetAddress ip)
	{
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(ip, CFG.SERVER_PORT));
			setName("Server-Thread");
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
			byte[] data = new byte[CFG.PACKETSIZE];
			
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
					sendPacket(new Packet3ServerInfo(clients.toArray(new User[] {})), user);
					sendPacketToAllClients(packet);
				}
				catch (Exception e)
				{}
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
	
	public void sendPacket(Packet p, User u) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, u.getIP(), u.getPort());
		
		socket.send(packet);
	}
	
	public void shutdown()
	{}
}
