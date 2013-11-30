package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet1Reject extends Packet
{
	public static enum Cause
	{
		OUTDATEDCLIENT,
		OUTDATEDSERVER,
		USERNAMETAKEN,
		GAMERUNNING,
		FULL,
		
		;
	}
	
	private Cause cause;
	
	public Packet1Reject(Cause cause)
	{
		super(1);
		this.cause = cause;
	}
	
	public Packet1Reject(byte[] data)
	{
		super(1);
		cause = Cause.values()[Integer.parseInt(readData(data))];
	}
	
	public Cause getCause()
	{
		return cause;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return (cause.ordinal() + "").getBytes();
	}
}
