package de.dakror.spamwars.net.packet;

/**
 * Is being used as connect request and client-side discovery broadcast
 * 
 * @author Dakror
 */
public class Packet14Discovery extends Packet
{
	private long version;
	
	public Packet14Discovery(byte[] data)
	{
		super(14);
		
		version = Long.parseLong(readData(data));
	}
	
	public Packet14Discovery(long version)
	{
		super(14);
		this.version = version;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return ("" + version).getBytes();
	}
	
	public long getVersion()
	{
		return version;
	}
}
