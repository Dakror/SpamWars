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
public class Packet03Attribute extends Packet {
	String key;
	String value;
	
	/**
	 * Key format: class_field_type
	 */
	public Packet03Attribute(String key, Object value) {
		super(3);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet03Attribute(byte[] data) {
		super(3);
		String[] s = readData(data).split("#~#");
		key = s[0];
		value = s[1];
	}
	
	@Override
	protected byte[] getPacketData() {
		return (key + "#~#" + value).getBytes();
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
