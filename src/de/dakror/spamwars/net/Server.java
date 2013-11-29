package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.InetAddress;

import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class Server extends NetHandler
{
	public Server(InetAddress ip)
	{
		super(ip);
		
		setName("Server-Thread");
		
		start();
	}
	
	@Override
	public void parsePacket(byte[] data)
	{}
	
	@Override
	public void sendPacket(Packet p) throws IOException
	{}
	
	@Override
	public void shutdown()
	{}
}
