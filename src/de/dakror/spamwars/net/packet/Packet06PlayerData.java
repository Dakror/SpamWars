package de.dakror.spamwars.net.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.weapon.WeaponType;

/**
 * @author Dakror
 */
public class Packet06PlayerData extends Packet
{
	Vector position;
	boolean left;
	int style, frame, life;
	float rot;
	int weaponID;
	String username;
	
	public Packet06PlayerData(Player p)
	{
		super(6);
		position = p.getPos();
		left = p.lookingLeft;
		style = p.getStyle();
		frame = p.frame;
		life = p.getLife();
		rot = p.getWeapon().rot;
		weaponID = p.getWeapon().type.ordinal();
		username = p.getUser().getUsername();
	}
	
	public Packet06PlayerData(byte[] data)
	{
		super(6);
		
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 1, data.length));
		position = new Vector(bb.getFloat(), bb.getFloat());
		left = bb.get() == (byte) -127;
		style = bb.get();
		frame = bb.get();
		rot = bb.getFloat();
		life = bb.getInt();
		weaponID = bb.getInt();
		
		int length = bb.getInt();
		
		byte[] str = new byte[length];
		bb.get(str, 0, length);
		
		username = new String(str);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		ByteBuffer bb = ByteBuffer.allocate(29 + 4 + username.length());
		bb.putFloat(position.x);
		bb.putFloat(position.y);
		bb.put(left ? (byte) -127 : (byte) -128);
		bb.put((byte) (style - 128));
		bb.put((byte) (frame - 128));
		bb.putFloat(rot);
		bb.putInt(life);
		bb.putInt(weaponID);
		
		bb.putInt(username.length());
		bb.put(username.getBytes());
		
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
	
	public WeaponType getWeaponType()
	{
		return WeaponType.values()[weaponID];
	}
	
	public String getUsername()
	{
		return username;
	}
}
