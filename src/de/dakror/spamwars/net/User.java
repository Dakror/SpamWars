package de.dakror.spamwars.net;

import java.net.InetAddress;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dakror
 */
public class User
{
	private InetAddress ip;
	private int port;
	private String username;
	
	public User(String username, InetAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.username = username;
	}
	
	public User(JSONObject o)
	{
		try
		{
			ip = InetAddress.getByName(o.getString("ip"));
			port = o.getInt("port");
			username = o.getString("username");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
	public void setIP(InetAddress ip)
	{
		this.ip = ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String serialize()
	{
		JSONObject o = new JSONObject();
		
		try
		{
			o.put("username", username);
			o.put("ip", ip.getHostAddress());
			o.put("port", port);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		
		return o.toString();
	}
	
	@Override
	public String toString()
	{
		return serialize().toString();
	}
}
