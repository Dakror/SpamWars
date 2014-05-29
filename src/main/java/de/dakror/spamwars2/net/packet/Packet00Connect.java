package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet00Connect extends Packet
{
	private String username;
	private long version;
	
	public Packet00Connect(byte[] data)
	{
		super(0);
		load(data);
		String[] s = readData(data).split(":");
		username = s[0];
		version = Long.parseLong(s[1]);
	}
	
	public Packet00Connect(String username, long version, boolean forServer)
	{
		super(0, forServer);
		this.version = version;
		this.username = username;
	}
	
	@Override
	public byte[] getPacketData()
	{
		return (username + ":" + version).getBytes();
	}
	
	public long getVersion()
	{
		return version;
	}
	
	public String getUsername()
	{
		return username;
	}
}
