package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet02Reject extends Packet
{
	public static enum Cause
	{
		USERNAMETAKEN("Dieser Benutzername ist auf diesem Server bereits vergeben."),
		GAMERUNNING("Das Spiel auf diesem Server hat bereits begonnen."),
		FULL("Der Server hat die maximale Anzahl an Spielern bereits erreicht."),
		ALREADYHOSTING("Du hostest bereist ein Spiel."),
		NOTHOSTING("Das Spiel, welchem du beitreten willst, konnte nicht gefunden werden."),
		INVALIDLOGIN("Login inkorrekt!"),
		
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
	
	public Packet02Reject(Cause cause, boolean forServer)
	{
		super(2, forServer);
		this.cause = cause;
	}
	
	public Packet02Reject(byte[] data)
	{
		super(2);
		load(data);
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
