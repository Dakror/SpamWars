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
	Vector velocity, position;
	boolean left;
	int style, frame;
	float rot;
	
	public Packet5PlayerData(Player p)
	{
		super(5);
		velocity = p.getVelocity();
		position = p.getPos();
		left = p.lookingLeft;
		style = p.getStyle();
		frame = p.frame;
		rot = p.getWeapon().rot;
	}
	
	public Packet5PlayerData(byte[] data)
	{
		super(5);
		
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(data, 1, data.length));
		position = new Vector(bb.getFloat(), bb.getFloat());
		velocity = new Vector(bb.getFloat(), bb.getFloat());
		left = bb.get() == (byte) -127;
		style = bb.get();
		frame = bb.get();
		rot = bb.getFloat();
	}
	
	@Override
	protected byte[] getPacketData()
	{
		ByteBuffer bb = ByteBuffer.allocate(23);
		bb.putFloat(position.x);
		bb.putFloat(position.y);
		bb.putFloat(velocity.x);
		bb.putFloat(velocity.y);
		bb.put(left ? (byte) -127 : (byte) -128);
		bb.put((byte) (style - 128));
		bb.put((byte) (frame - 128));
		bb.putFloat(rot);
		
		return bb.array();
	}
	
	public Vector getVelocity()
	{
		return velocity;
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
}
