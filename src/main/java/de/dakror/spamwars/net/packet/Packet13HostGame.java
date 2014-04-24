package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet13HostGame extends Packet
{
	public Packet13HostGame()
	{
		super(13, false);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return "hi".getBytes();
	}
	
}
