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
public class Packet00Connect extends Packet {
	private String username;
	private long version;
	
	public Packet00Connect(byte[] data) {
		super(0);
		String[] s = readData(data).split(":");
		username = s[0];
		version = Long.parseLong(s[1]);
	}
	
	public Packet00Connect(String username, long version) {
		super(0);
		this.version = version;
		this.username = username;
	}
	
	@Override
	public byte[] getPacketData() {
		return (username + ":" + version).getBytes();
	}
	
	public long getVersion() {
		return version;
	}
	
	public String getUsername() {
		return username;
	}
}
