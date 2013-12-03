package de.dakror.spamwars.net.packet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.net.User;

/**
 * @author Dakror
 */
public class Packet04ServerInfo extends Packet
{
	User[] users;
	
	public Packet04ServerInfo(byte[] data)
	{
		super(4);
		try
		{
			JSONArray arr = new JSONArray(readData(data));
			users = new User[arr.length()];
			for (int i = 0; i < arr.length(); i++)
			{
				users[i] = new User(arr.getJSONObject(i));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public Packet04ServerInfo(User[] users)
	{
		super(4);
		this.users = users;
	}
	
	public Packet04ServerInfo()
	{
		super(4);
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
				json.put(new JSONObject(p.serializeThin()));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return json.toString().getBytes();
	}
}
