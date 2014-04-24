package de.dakror.spamwars.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.dakror.spamwars.net.User;

/**
 * @author Dakror
 */
public class Packet04PlayerList extends Packet
{
	User[] users;
	
	public Packet04PlayerList(byte[] data)
	{
		super(4);
		load(data);
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		bb.get(); // skip forServer
		int c = bb.get();
		
		users = new User[c];
		
		for (int i = 0; i < c; i++)
		{
			int K = bb.getShort();
			int D = bb.getShort();
			int len = bb.get();
			byte[] str = new byte[len];
			bb.get(str, 0, len);
			users[i] = new User(new String(str), K, D);
		}
	}
	
	public Packet04PlayerList(User[] users, boolean forServer)
	{
		super(4, forServer);
		this.users = users;
	}
	
	public Packet04PlayerList()
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
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(users.length);
		for (User u : users)
		{
			try
			{
				baos.write(u.getBytes());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return baos.toByteArray();
	}
}
