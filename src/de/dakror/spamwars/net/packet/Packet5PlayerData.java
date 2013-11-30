package de.dakror.spamwars.net.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.entity.Player;

/**
 * @author Dakror
 */
public class Packet5PlayerData extends Packet
{
	Vector position;
	boolean left;
	int style, frame, life;
	float rot;
	
	public Packet5PlayerData(Player p)
	{
		super(5);
		position = p.getPos();
		left = p.lookingLeft;
		style = p.getStyle();
		frame = p.frame;
		life = p.getLife();
		rot = p.getWeapon().rot;
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
}
