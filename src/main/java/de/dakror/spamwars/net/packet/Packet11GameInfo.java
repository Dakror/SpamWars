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

/**
 * @author Dakror
 */
public class Packet11GameInfo extends Packet {
	public enum GameMode {
		DEATHMATCH("Deathmatch"),
		ONE_IN_THE_CHAMBER("Eine im Lauf"),
		
		;
		
		private String name;
		
		private GameMode(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	int minutes;
	GameMode mode;
	
	public Packet11GameInfo(int minutes, GameMode mode) {
		super(11);
		this.minutes = minutes;
		this.mode = mode;
	}
	
	public Packet11GameInfo(byte[] data) {
		super(11);
		String[] parts = readData(data).split(":");
		
		minutes = Integer.parseInt(parts[0]);
		mode = GameMode.values()[Integer.parseInt(parts[1])];
	}
	
	@Override
	protected byte[] getPacketData() {
		return (minutes + ":" + mode.ordinal()).getBytes();
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public GameMode getGameMode() {
		return mode;
	}
	
}
