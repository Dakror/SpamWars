package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet02Reject.Cause;
import de.dakror.spamwars.net.packet.Packet14Login;

/**
 * @author Dakror
 */
public class CentralServer
{
	static class AFKManager extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				for (User u : users)
				{
					if (System.currentTimeMillis() - u.lastInteraction > 2000)
					{
						try
						{
							sendPacket(new Packet01Disconnect(u.getUsername(), de.dakror.spamwars.net.packet.Packet01Disconnect.Cause.KICK, false), u);
							out("User timed out: " + u.getUsername());
							users.remove(u);
							hosts.remove(u);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static final int PORT = 19950;
	static DatagramSocket socket;
	static CopyOnWriteArrayList<User> hosts;
	static CopyOnWriteArrayList<User> users;
	
	public static void main(String[] args)
	{
		hosts = new CopyOnWriteArrayList<>();
		users = new CopyOnWriteArrayList<>();
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
			out("Server started");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		while (true)
		{
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data, packet.getAddress(), packet.getPort());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void parsePacket(byte[] data, InetAddress address, int port)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		User user = null;
		for (User u : users)
		{
			if (u.getIP().equals(address) && u.getPort() == port)
			{
				user = u;
				break;
			}
		}
		
		if (user == null && type != PacketTypes.LOGIN)
		{
			out("Revoked unauthorized request: " + address.getHostName() + ":" + port);
			return;
		}
		
		switch (type)
		{
			case ALIVE:
			{
				user.lastInteraction = System.currentTimeMillis();
			}
			case LOGIN:
			{
				Packet14Login p = new Packet14Login(data);
				try
				{
					String s = Helper.getURLContent(new URL("http://dakror.de/mp-api/login_noip.php?username=" + p.getUsername() + "&password=" + p.getPwdMd5()));
					if (s.contains("true"))
					{
						out("User logged in: " + p.getUsername());
						users.add(new User(p.getUsername(), address, port));
					}
					else
					{
						out("Invalid login: " + address.getHostName() + ":" + port);
						sendPacket(new Packet02Reject(Cause.INVALIDLOGIN, false), user);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			case HOSTGAME:
			{
				for (User u : hosts)
				{
					if (u.getIP().equals(address) && u.getPort() == port)
					{
						try
						{
							sendPacket(new Packet02Reject(Cause.ALREADYHOSTING, false), user);
							out("Refused to host multiple games: " + u.getUsername());
							return;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				
				out("New game hosted: " + user.getUsername());
				hosts.add(user);
				break;
			}
			default:
				break;
		}
	}
	
	public static void sendPacket(Packet p, User user) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, user.getIP(), user.getPort());
		
		socket.send(packet);
	}
	
	public static void out(Object... p)
	{
		String timestamp = new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date());
		if (p.length == 1) System.out.println(timestamp + p[0]);
		else System.out.println(timestamp + Arrays.toString(p));
	}
}
