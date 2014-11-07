package de.dakror.spamwars.net.packet;

import java.awt.Point;
import java.nio.ByteBuffer;

import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Packet05Chunk extends Packet
{
	public static final int SIZE = 15;
	
	Point chunk;
	byte[] data;
	
	public Packet05Chunk(World world, Point chunk)
	{
		super(5);
		
		data = world.getData(chunk.x, chunk.y, SIZE);
		this.chunk = chunk;
	}
	
	public Packet05Chunk(byte[] data)
	{
		super(5);
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		
		chunk = new Point(bb.get(), bb.get());
		int length = bb.getInt();
		byte[] worlddata = new byte[length];
		bb.get(worlddata, 0, length);
		this.data = worlddata;
	}
	
	public byte[] getWorldData()
	{
		return data;
	}
	
	public Point getChunk()
	{
		return chunk;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		ByteBuffer bb = ByteBuffer.allocate(6 + data.length);
		bb.put((byte) chunk.x);
		bb.put((byte) chunk.y);
		bb.putInt(data.length);
		bb.put(data);
		
		return bb.array();
	}
}
