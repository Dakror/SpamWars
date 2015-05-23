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

import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public class Packet10EntityStatus extends Packet {
	Vector pos;
	boolean state;
	
	public Packet10EntityStatus(Vector pos, boolean state) {
		super(10);
		this.pos = pos;
		this.state = state;
	}
	
	public Packet10EntityStatus(byte[] data) {
		super(10);
		String[] parts = readData(data).split(":");
		pos = new Vector(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
		state = Boolean.parseBoolean(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData() {
		return (pos.x + ":" + pos.y + ":" + Boolean.toString(state)).getBytes();
	}
	
	public boolean getState() {
		return state;
	}
	
	public Vector getPos() {
		return pos;
	}
}
