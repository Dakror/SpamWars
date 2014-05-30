package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
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
import de.dakror.spamwars.net.packet.Packet13HostGame;
import de.dakror.spamwars.net.packet.Packet14Login;
import de.dakror.spamwars.net.packet.Packet16JoinGame;

/**
 * @author Dakror
 */
public class CentralServer
{
	public static class AFKManager extends Thread
	{
		public AFKManager()
		{
			setName("AFKManager");
			start();
		}
		
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
	
	public static final int PORT = 19951;
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
			new AFKManager();
			out("CentralServer started");
		}
		catch (BindException e)
		{
			out("There is a CentralServer running on this machine already!");
			System.exit(0);
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
		if (new String(data, 0, 2).equals("FF"))
		{
			ByteBuffer bb = ByteBuffer.wrap(data);
			bb.get();
			bb.get(); // skip marker
			byte[] addr = new byte[bb.getInt()];
			bb.get(addr);
			try
			{
				socket.send(new DatagramPacket(data, 10 + addr.length, data.length - 10 - addr.length, InetAddress.getByAddress(addr), bb.getInt()));
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
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
			out("Revoked unauthorized request: " + address.getHostAddress() + ":" + port);
			return;
		}
		
		switch (type)
		{
			case DISCONNECT:
			{
				Packet01Disconnect p = new Packet01Disconnect(data);
				if (p.getUsername().equals(user.getUsername()))
				{
					out("User disconnected: " + p.getUsername());
					users.remove(user);
					hosts.remove(user);
				}
				break;
			}
			case ALIVE:
			{
				user.lastInteraction = System.currentTimeMillis();
				break;
			}
			case LOGIN:
			{
				Packet14Login p = new Packet14Login(data);
				try
				{
					String s = Helper.getURLContent(new URL("http://dakror.de/mp-api/login_noip.php?username=" + p.getUsername() + "&password=" + p.getPwdMd5()));
					if (s.contains("true"))
					{
						out("User logged in: " + p.getUsername() + " from " + address.getHostAddress() + ":" + port);
						User u = new User(p.getUsername(), address, port);
						users.add(u);
						sendPacket(p, u);
					}
					else
					{
						out("Invalid login: " + address.getHostAddress() + ":" + port);
						sendPacket(new Packet02Reject(Cause.INVALIDLOGIN, false), user);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			case HOSTGAME:
			{
				Packet13HostGame p = new Packet13HostGame(data);
				if (p.isStart())
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
				}
				else
				{
					for (User u : hosts)
					{
						if (u.getIP().equals(address) && u.getPort() == port)
						{
							out("Ended game from: " + u.getUsername());
							hosts.remove(u);
							return;
						}
					}
				}
				break;
			}
			case JOINGAME:
			{
				try
				{
					Packet16JoinGame p = new Packet16JoinGame(data);
					for (User u : hosts)
					{
						if (u.getUsername().equals(p.getUsername()))
						{
							
							sendPacket(new Packet16JoinGame(u.getIP(), u.getPort()), user);
							out("User " + user.getUsername() + " joins game from " + u.getUsername());
							return;
						}
					}
					
					sendPacket(new Packet02Reject(Cause.NOTHOSTING, false), user);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
