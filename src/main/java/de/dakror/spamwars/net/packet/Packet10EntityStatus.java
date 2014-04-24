package de.dakror.spamwars.net.packet;

import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public class Packet10EntityStatus extends Packet
{
	Vector pos;
	boolean state;
	
	public Packet10EntityStatus(Vector pos, boolean state, boolean forServer)
	{
		super(10, forServer);
		this.pos = pos;
		this.state = state;
	}
	
	public Packet10EntityStatus(byte[] data)
	{
		super(10);
		load(data);
		String[] parts = readData(data).split(":");
		pos = new Vector(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
		state = Boolean.parseBoolean(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (pos.x + ":" + pos.y + ":" + Boolean.toString(state)).getBytes();
	}
	
	public boolean getState()
	{
		return state;
	}
	
	public Vector getPos()
	{
		return pos;
	}
}
