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
 

package de.dakror.spamwars.game.anim;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class Animation implements Drawable {
	String name;
	Vector pos;
	
	public float speed;
	float rotation;
	
	public int startTick;
	public int frame;
	int size;
	public int frames;
	
	boolean dead;
	
	public Animation(String name, Vector pos, float speed, int size, int frames) {
		this(name, pos, speed, 0, size, frames);
	}
	
	public Animation(String name, Vector pos, float speed, float rotation, int size, int frames) {
		this.name = name;
		this.pos = pos;
		this.speed = speed;
		this.rotation = rotation;
		this.size = size;
		this.frames = frames;
		
		frame = 0;
		dead = false;
	}
	
	public Animation(JSONObject o) {
		try {
			pos = new Vector((float) o.getDouble("x"), (float) o.getDouble("y"));
			name = o.getString("name");
			speed = (float) o.getDouble("speed");
			rotation = (float) o.getDouble("rot");
			startTick = o.getInt("start");
			size = o.getInt("size");
			frames = o.getInt("frames");
			
			dead = false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject serialize() {
		JSONObject o = new JSONObject();
		try {
			o.put("x", pos.x);
			o.put("y", pos.y);
			o.put("name", name);
			o.put("speed", speed);
			o.put("rot", rotation);
			o.put("start", startTick);
			o.put("size", size);
			o.put("frames", frames);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (dead) return;
		
		Image i = Game.getImage("anim/" + name + ".png");
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(-Game.w.getInsets().left, -Game.w.getInsets().top);
		at.rotate(rotation, (int) (Game.world.x + pos.x) + size / 2, (int) (Game.world.y + pos.y) + size / 2);
		g.setTransform(at);
		
		Helper.drawImage(i, (int) (Game.world.x + pos.x), (int) (Game.world.y + pos.y), size, size, i.getHeight(null) * frame, 0, i.getHeight(null), i.getHeight(null), g);
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick) {
		if (dead) return;
		
		if (startTick == 0) {
			startTick = tick;
			return;
		}
		
		if ((startTick - tick) % speed == 0) frame++;
		
		if (frame >= frames) dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
}
