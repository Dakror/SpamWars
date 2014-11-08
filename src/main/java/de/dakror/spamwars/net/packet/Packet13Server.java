package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet13Server extends Packet
{
	private String hostName;
	/**
	 * if joining isn't possible anymore due to the game starting,
	 * this will be -1
	 */
	private int players;
	
	public Packet13Server(byte[] data)
	{
		super(13);
		String[] s = readData(data).split(":");
		hostName = s[0];
		players = Integer.parseInt(s[1]);
	}
	
	public Packet13Server(String hostName, int players)
	{
		super(13);
		
		this.hostName = hostName;
		this.players = players;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (hostName + ":" + players).getBytes();
	}
	
	public String getHostName()
	{
		return hostName;
	}
	
	public int getPlayers()
	{
		return players;
	}
	
	public void setPlayers(int players)
	{
		this.players = players;
	}
}
