package de.dakror.spamwars.net.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import de.dakror.gamesetup.util.Compressor;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Packet4World extends Packet
{
	World world;
	
	public Packet4World(World world)
	{
		super(4);
		
		this.world = world;
	}
	
	public Packet4World(byte[] data)
	{
		super(4);
		
		byte[] d = Arrays.copyOfRange(data, 1, data.length);
		ByteBuffer bb = ByteBuffer.wrap(d);
		int w = bb.getInt();
		int h = bb.getInt();
		int length = bb.getInt();
		byte[] worlddata = new byte[length];
		bb.get(worlddata, 0, length);
		worlddata = Compressor.decompress(worlddata);
		
		world = new World(w, h, worlddata);
	}
	
	public World getWorld()
	{
		return world;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		byte[] data = Compressor.compress(world.getData());
		ByteBuffer bb = ByteBuffer.allocate(12 + data.length);
		bb.putInt(world.width / Tile.SIZE);
		bb.putInt(world.height / Tile.SIZE);
		bb.putInt(data.length);
		bb.put(data);
		
		return bb.array();
	}
}
