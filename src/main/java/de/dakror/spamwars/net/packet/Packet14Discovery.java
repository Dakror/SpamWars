package de.dakror.spamwars.net.packet;

/**
 * Is being used as connect request and client-side discovery broadcast
 * 
 * @author Dakror
 */
public class Packet14Discovery extends Packet
{
	private int version;
	
	public Packet14Discovery(byte[] data)
	{
		super(14);
		
		version = Integer.parseInt(readData(data));
	}
	
	public Packet14Discovery(int version)
	{
		super(14);
		this.version = version;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return ("" + version).getBytes();
	}
	
	public int getVersion()
	{
		return version;
	}
}
