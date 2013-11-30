package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet0Connect extends Packet
{
	private String username;
	private int version;
	
	public Packet0Connect(byte[] data)
	{
		super(0);
		String[] s = readData(data).split(":");
		username = s[0];
		version = Integer.parseInt(s[1]);
	}
	
	public Packet0Connect(String username, int version)
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
