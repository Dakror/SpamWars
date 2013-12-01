package de.dakror.spamwars.net.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Compressor;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.net.User;

/**
 * @author Dakror
 */
public class Packet5PlayerData extends Packet
{
	Vector position;
	boolean left;
	int style, frame, life;
	float rot;
	User user;
	
	public Packet5PlayerData(Player p)
	{
		super(5);
		position = p.getPos();
		left = p.lookingLeft;
		style = p.getStyle();
		frame = p.frame;
		life = p.getLife();
		rot = p.getWeapon().rot;
		user = p.getUser();
	}
	
	public Packet5PlayerData(byte[] data)
	{
		super(5);
		
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 1, data.length));
		position = new Vector(bb.getFloat(), bb.getFloat());
		left = bb.get() == (byte) -127;
		style = bb.get();
		frame = bb.get();
		rot = bb.getFloat();
		life = bb.getInt();
		int length = bb.getInt();
		byte[] str = new byte[length];
		bb.get(str, 0, length);
		str = Compressor.decompress(str);
		try
		{
			user = new User(new JSONObject(new String(str)));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected byte[] getPacketData()
	{
		ByteBuffer bb = ByteBuffer.allocate(25);
		bb.putFloat(position.x);
		bb.putFloat(position.y);
		bb.put(left ? (byte) -127 : (byte) -128);
		bb.put((byte) (style - 128));
		bb.put((byte) (frame - 128));
		bb.putFloat(rot);
		bb.putInt(life);
		
		byte[] str = Compressor.compress(user.toString().getBytes());
		
		bb.putInt(str.length);
		bb.put(str);
		
		return bb.array();
	}
	
	public Vector getPosition()
	{
		return position;
	}
	
	public boolean isLeft()
	{
		return left;
	}
	
	public int getStyle()
	{
		return style;
	}
	
	public int getFrame()
	{
		return frame;
	}
	
	public float getRot()
	{
		return rot;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public User getUser()
	{
		return user;
	}
}
