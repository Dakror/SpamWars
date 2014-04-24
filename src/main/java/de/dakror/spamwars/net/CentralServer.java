package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet02Reject.Cause;
import de.dakror.spamwars.net.packet.Packet14Login;

/**
 * @author Dakror
 */
public class CentralServer
{
	public static final int PORT = 19950;
	static DatagramSocket socket;
	static ArrayList<User> hosts;
	static ArrayList<User> users;
	
	public static void main(String[] args)
	{
		hosts = new ArrayList<>();
		users = new ArrayList<>();
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
						sendPacket(new Packet02Reject(Cause.INVALIDLOGIN, false), address, port);
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
							sendPacket(new Packet02Reject(Cause.ALREADYHOSTING, false), address, port);
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
	
	public static void sendPacket(Packet p, InetAddress ip, int port) throws IOException
	{
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		
		socket.send(packet);
	}
	
	public static void out(Object... p)
	{
		String timestamp = new SimpleDateFormat("'['HH:mm:ss']: '").format(new Date());
		if (p.length == 1) System.out.println(timestamp + p[0]);
		else System.out.println(timestamp + Arrays.toString(p));
	}
}
