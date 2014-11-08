package de.dakror.spamwars.net.packet;

/**
 * Is being used as connect request and client-side discovery broadcast
 * 
 * @author Dakror
 */
public class Packet00Connect extends Packet
{
	private String username;
	private int version;
	
	public Packet00Connect(byte[] data)
	{
		super(0);
		String[] s = readData(data).split(":");
		username = s[0];
		version = Integer.parseInt(s[1]);
	}
	
	public Packet00Connect(String username, int version)
	{
		super(0);
		this.version = version;
		this.username = username;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return (username + ":" + version).getBytes();
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public String getUsername()
	{
		return username;
	}
}
