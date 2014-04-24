package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet02Reject extends Packet
{
	public static enum Cause
	{
		OUTDATEDCLIENT("Dein Client läuft auf einer älteren Version als der Server."),
		OUTDATEDSERVER("Der Server läuft auf einer alteren Version als dein Client."),
		USERNAMETAKEN("Dieser Benutzername ist auf diesem Server bereits vergeben."),
		GAMERUNNING("Das Spiel auf diesem Server hat bereits begonnen."),
		FULL("Der Server hat die maximale Anzahl an Spielern bereits erreicht."),
		
		;
		private String description;
		
		private Cause(String desc)
		{
			description = desc;
		}
		
		public String getDescription()
		{
			return description;
		}
	}
	
	private Cause cause;
	
	public Packet02Reject(Cause cause)
	{
		super(2);
		this.cause = cause;
	}
	
	public Packet02Reject(byte[] data)
	{
		super(2);
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
