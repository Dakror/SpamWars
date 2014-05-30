package de.dakror.spamwars.net.packet;


/**
 * @author Dakror
 */
public class Packet14Login extends Packet
{
	String username, pwdMd5;
	
	public Packet14Login(String username, String pwdMd5)
	{
		super(14, false);
		this.username = username;
		this.pwdMd5 = pwdMd5;
	}
	
	public Packet14Login(byte[] data)
	{
		super(14);
		load(data);
		String[] parts = readData(data).split(":");
		
		username = parts[0];
		pwdMd5 = parts[1];
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPwdMd5()
	{
		return pwdMd5;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (username + ":" + pwdMd5).getBytes();
	}
	
}
