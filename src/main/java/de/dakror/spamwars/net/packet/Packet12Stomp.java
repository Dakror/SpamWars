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
public class Packet12Stomp extends Packet {
	String username;
	String stomped;
	float damage;
	
	public Packet12Stomp(String username, String stomped, float damage) {
		super(12);
		this.username = username;
		this.stomped = stomped;
		this.damage = damage;
	}
	
	public Packet12Stomp(byte[] data) {
		super(12);
		String[] parts = readData(data).split(":");
		
		username = parts[0];
		stomped = parts[1];
		damage = Float.parseFloat(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData() {
		return (username + ":" + stomped + ":" + damage).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getStomped() {
		return stomped;
	}
	
	public float getDamage() {
		return damage;
	}
	
}
