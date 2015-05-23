/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.spamwars.net.packet;

import java.awt.Point;
import java.nio.ByteBuffer;

import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Packet05Chunk extends Packet {
	public static final int SIZE = 15;
	
	Point chunk;
	byte[] data;
	
	public Packet05Chunk(World world, Point chunk) {
		super(5);
		
		data = world.getData(chunk.x, chunk.y, SIZE);
		this.chunk = chunk;
	}
	
	public Packet05Chunk(byte[] data) {
		super(5);
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		
		chunk = new Point(bb.get(), bb.get());
		int length = bb.getInt();
		byte[] worlddata = new byte[length];
		bb.get(worlddata, 0, length);
		this.data = worlddata;
	}
	
	public byte[] getWorldData() {
		return data;
	}
	
	public Point getChunk() {
		return chunk;
	}
	
	@Override
	protected byte[] getPacketData() {
		ByteBuffer bb = ByteBuffer.allocate(6 + data.length);
		bb.put((byte) chunk.x);
		bb.put((byte) chunk.y);
		bb.putInt(data.length);
		bb.put(data);
		
		return bb.array();
	}
}
