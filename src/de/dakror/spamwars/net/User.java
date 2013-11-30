package de.dakror.spamwars.net;

import java.net.InetAddress;

/**
 * @author Dakror
 */
public class User
{
	InetAddress ip;
	int port;
	String username;
	
	public User(String username, InetAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.username = username;
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
	public void setIP(InetAddress ip)
	{
		this.ip = ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
}
