package de.dakror.spamwars.net.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Dakror
 */
public class Packet16JoinGame extends Packet
{
	String username;
	InetAddress hostIp;
	int hostPort;
	
	public Packet16JoinGame(String username)
	{
		super(16, false);
		this.username = username;
	}
	
	public Packet16JoinGame(InetAddress ip, int port)
	{
		super(16, false);
		hostIp = ip;
		hostPort = port;
	}
	
	public Packet16JoinGame(byte[] data)
	{
		super(16);
		load(data);
		String d = readData(data);
		if (d.contains(":"))
		{
			String[] parts = d.split(":");
			try
			{
				hostIp = InetAddress.getByName(new String(parts[0]));
				hostPort = Integer.parseInt(new String(parts[1]));
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		else username = d;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public InetAddress getHostIp()
	{
		return hostIp;
	}
	
	public int getHostPort()
	{
		return hostPort;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (hostIp == null) return username.getBytes();
		return (hostIp.getHostAddress() + ":" + hostPort).getBytes();
	}
}
