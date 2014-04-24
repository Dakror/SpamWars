package de.dakror.spamwars.net.central;

import java.net.InetAddress;

/**
 * @author Dakror
 */
public class Game
{
	public int hostId;
	public InetAddress ip;
	public int port;
	
	public Game(int hostId, InetAddress ip, int port)
	{
		this.hostId = hostId;
		this.ip = ip;
		this.port = port;
	}
}
