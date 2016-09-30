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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.dakror.spamwars.net.User;

/**
 * @author Dakror
 */
public class Packet04PlayerList extends Packet {
	User[] users;
	
	public Packet04PlayerList(byte[] data) {
		super(4);
		
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.get(); // skip id
		int c = bb.get();
		
		users = new User[c];
		
		for (int i = 0; i < c; i++) {
			int K = bb.getShort();
			int D = bb.getShort();
			int len = bb.get();
			byte[] str = new byte[len];
			bb.get(str, 0, len);
			users[i] = new User(new String(str), K, D);
		}
	}
	
	public Packet04PlayerList(User[] users) {
		super(4);
		this.users = users;
	}
	
	public Packet04PlayerList() {
		super(4);
	}
	
	public User[] getUsers() {
		return users;
	}
	
	@Override
	protected byte[] getPacketData() {
		if (users == null) return "".getBytes();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(users.length);
		for (User u : users) {
			try {
				baos.write(u.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return baos.toByteArray();
	}
}
