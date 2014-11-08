package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet13Server extends Packet
{
	private String hostName;
	/**
	 * encoding:<br>
	 * >= 0 = actual player count<br>
	 * -1 = game has started already<br>
	 * -2 = game version not matching<br>
	 */
	private int value;
	
	public Packet13Server(byte[] data)
	{
		super(13);
		String[] s = readData(data).split(":");
		hostName = s[0];
		value = Integer.parseInt(s[1]);
	}
	
	public Packet13Server(String hostName, int players)
	{
		super(13);
		
		this.hostName = hostName;
		this.value = players;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (hostName + ":" + value).getBytes();
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getPlayers()
	{
		return value;
	}
	
	public void setPlayers(int players)
	{
		this.value = players;
	}
}
