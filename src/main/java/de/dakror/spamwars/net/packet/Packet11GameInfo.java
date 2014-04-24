package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet11GameInfo extends Packet
{
	public enum GameMode
	{
		DEATHMATCH("Deathmatch"),
		ONE_IN_THE_CHAMBER("Eine im Lauf"),
		
		;
		
		private String name;
		
		private GameMode(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	int minutes;
	GameMode mode;
	
	public Packet11GameInfo(int minutes, GameMode mode)
	{
		super(11);
		this.minutes = minutes;
		this.mode = mode;
	}
	
	public Packet11GameInfo(byte[] data)
	{
		super(11);
		String[] parts = readData(data).split(":");
		
		minutes = Integer.parseInt(parts[0]);
		mode = GameMode.values()[Integer.parseInt(parts[1])];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (minutes + ":" + mode.ordinal()).getBytes();
	}
	
	public int getMinutes()
	{
		return minutes;
	}
	
	public GameMode getGameMode()
	{
		return mode;
	}
	
}
