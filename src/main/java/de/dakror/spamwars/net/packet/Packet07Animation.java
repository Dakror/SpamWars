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

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.game.anim.Animation;


/**
 * @author Dakror
 */
public class Packet07Animation extends Packet {
	Animation animation;
	
	public Packet07Animation(Animation anim) {
		super(7);
		animation = anim;
	}
	
	public Packet07Animation(byte[] data) {
		super(7);
		
		try {
			animation = new Animation(new JSONObject(readData(data)));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	@Override
	protected byte[] getPacketData() {
		return animation.serialize().toString().getBytes();
	}
}
