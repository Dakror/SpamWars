package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet13HostGame extends Packet
{
	boolean start;
	
	public Packet13HostGame(boolean start)
	{
		super(13, false);
		this.start = start;
	}
	
	public Packet13HostGame(byte[] data)
	{
		super(13);
		load(data);
		start = Boolean.parseBoolean(readData(data));
	}
	
	public boolean isStart()
	{
		return start;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return Boolean.toString(start).getBytes();
	}
}
