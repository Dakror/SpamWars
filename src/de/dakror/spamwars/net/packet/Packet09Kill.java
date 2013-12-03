package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet09Kill extends Packet
{
	String killer, dead;
	
	public Packet09Kill(String killer, String dead)
	{
		super(9);
		
		this.killer = killer;
		this.dead = dead;
	}
	
	public Packet09Kill(byte[] data)
	{
		super(9);
		
		String[] parts = readData(data).split(":");
		killer = parts[0];
		dead = parts[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (killer + ":" + dead).getBytes();
	}
	
	public String getKiller()
	{
		return killer;
	}
	
	public String getDead()
	{
		return dead;
	}
}
