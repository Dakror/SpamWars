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


package de.dakror.spamwars.net;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Dakror
 */
public class User {
	private InetAddress ip;
	private int port;
	private String username;
	
	public int K, D;
	
	public User(String username, InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		this.username = username;
		
		K = D = 0;
	}
	
	public User(JSONObject o) {
		try {
			if (o.has("i")) ip = InetAddress.getByName(o.getString("i"));
			if (o.has("p")) port = o.getInt("p");
			username = o.getString("u");
			K = o.getInt("K");
			D = o.getInt("D");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public User(String username, int K, int D) {
		this.username = username;
		this.K = K;
		this.D = D;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public void setIP(InetAddress ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String serialize() {
		JSONObject o = new JSONObject();
		
		try {
			o.put("u", username);
			o.put("i", ip.getHostAddress());
			o.put("p", port);
			o.put("K", K);
			o.put("D", D);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return o.toString();
	}
	
	public byte[] getBytes() {
		ByteBuffer bb = ByteBuffer.allocate(username.length() + 5);
		bb.putShort((short) K);
		bb.putShort((short) D);
		bb.put((byte) username.length());
		bb.put(username.getBytes());
		
		return bb.array();
	}
	
	@Override
	public String toString() {
		return serialize().toString();
	}
}
