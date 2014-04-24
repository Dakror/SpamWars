package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet01Disconnect extends Packet
{
	public enum Cause
	{
		SERVER_CLOSED("Der Server wurde geschlossen."),
		USER_DISCONNECT("Spiel beendet."),
		
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
	private String username;
	
	public Packet01Disconnect(byte[] data)
	{
		super(1);
		load(data);
		String[] s = readData(data).split(":");
		username = s[0];
		cause = Cause.values()[Integer.parseInt(s[1])];
	}
	
	public Packet01Disconnect(String username, Cause cause, boolean forServer)
	{
		super(1, forServer);
		this.username = username;
		this.cause = cause;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return (username + ":" + cause.ordinal()).getBytes();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public Cause getCause()
	{
		return cause;
	}
}
