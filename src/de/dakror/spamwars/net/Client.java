package de.dakror.spamwars.net;

import java.io.IOException;

import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class Client extends NetHandler
{
	public Client()
	{
		super();
		setName("Client-Thread");
		
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
