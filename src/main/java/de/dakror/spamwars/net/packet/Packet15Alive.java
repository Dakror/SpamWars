package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet15Alive extends Packet
{
	public Packet15Alive(boolean forServer)
	{
		super(15, forServer);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return "ping".getBytes();
	}
}
