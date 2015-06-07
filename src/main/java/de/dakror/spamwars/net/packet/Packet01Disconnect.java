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
public class Packet01Disconnect extends Packet {
	public enum Cause {
		SERVER_CLOSED("Der Server wurde geschlossen."),
		USER_DISCONNECT("Spiel beendet."),
		
		;
		private String description;
		
		private Cause(String desc) {
			description = desc;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	private Cause cause;
	private String username;
	
	public Packet01Disconnect(byte[] data) {
		super(1);
		String[] s = readData(data).split(":");
		username = s[0];
		cause = Cause.values()[Integer.parseInt(s[1])];
	}
	
	public Packet01Disconnect(String username, Cause cause) {
		super(1);
		this.username = username;
		this.cause = cause;
	}
	
	@Override
	public byte[] getPacketData() {
		return (username + ":" + cause.ordinal()).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public Cause getCause() {
		return cause;
	}
}
