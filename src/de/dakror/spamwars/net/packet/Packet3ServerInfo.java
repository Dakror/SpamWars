package de.dakror.spamwars.net.packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.net.User;

/**
 * @author Dakror
 */
public class Packet3ServerInfo extends Packet
{
	User[] users;
	
	public Packet3ServerInfo(byte[] data)
	{
		super(3);
	}
	
	public Packet3ServerInfo(User[] users)
	{
		super(3);
		this.users = users;
	}
	
	public Packet3ServerInfo()
	{
		super(3);
	}
	
	public User[] getUsers()
	{
		return users;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		if (users == null) return "".getBytes();
		
		JSONArray json = new JSONArray();
		for (User p : users)
		{
			try
			{
				json.put(new JSONObject(p.serialize()));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return json.toString().getBytes();
	}
}
