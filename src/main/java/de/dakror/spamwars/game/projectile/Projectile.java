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
 

package de.dakror.spamwars.game.projectile;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class Projectile implements Drawable {
	private Vector pos, target;
	Rectangle tex;
	int damage, range, speed;
	String username;
	
	private boolean dead;
	
	private float rot;
	
	public Projectile(Vector pos, Vector target, String username, int speed, int range, int damage) {
		tex = new Rectangle(275, 89, 20, 10);
		this.speed = speed;
		this.range = range;
		this.damage = damage;
		this.pos = pos;
		this.username = username;
		this.target = target;
		
		Vector dif = target.clone().sub(pos);
		dif.setLength(range);
		this.target = pos.clone().add(dif);
	}
	
	public Projectile(JSONObject o) throws JSONException {
		this(new Vector((float) o.getDouble("x"), (float) o.getDouble("y")), new Vector((float) o.getDouble("tx"), (float) o.getDouble("ty")), o.getString("u"), o.getInt("s"),
					o.getInt("r"), o.getInt("dm"));
	}
	
	public JSONObject serialize() {
		JSONObject o = new JSONObject();
		try {
			o.put("x", pos.x);
			o.put("y", pos.y);
			o.put("tx", target.x);
			o.put("ty", target.y);
			o.put("r", range);
			o.put("s", speed);
			o.put("dm", damage);
			o.put("d", dead);
			o.put("u", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (dead) return;
		
		Vector pos = this.pos.clone().add(new Vector(Game.world.x, Game.world.y));
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(-Game.w.getInsets().left, -Game.w.getInsets().top);
		at.rotate(rot, pos.x + tex.width / 2, pos.y + tex.height / 2);
		g.setTransform(at);
		Helper.drawImage(Game.getImage("weapon/projectiles.png"), (int) pos.x, (int) pos.y, tex.width, tex.height, tex.x, tex.y, tex.width, tex.height, g);
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick) {
		if (dead) return;
		
		Vector dif = target.clone().sub(pos);
		if (dif.getLength() > speed) dif.setLength(speed);
		
		rot = (float) Math.toRadians(dif.getAngleOnXAxis());
		
		Vector nextPos = pos.clone().add(dif);
		
		Point p = Game.world.getTile((int) nextPos.x, (int) nextPos.y);
		Tile tile = Tile.values()[Game.world.getTileIdAtPixel((int) nextPos.x, (int) nextPos.y)];
		
		Line2D line = new Line2D.Float(pos.x, pos.y, nextPos.x, nextPos.y);
		
		if (tile.getBump() != null) {
			Rectangle b = (Rectangle) tile.getBump().clone();
			b.translate(p.x * Tile.SIZE, p.y * Tile.SIZE);
			if (b.intersectsLine(line)) {
				dead = true;
				return;
			}
		} else if (tile.getLeftY() >= 0) {
			Polygon b = new Polygon();
			b.addPoint(0, tile.getLeftY());
			b.addPoint(Tile.SIZE, tile.getRightY());
			b.addPoint(Tile.SIZE, Tile.SIZE);
			b.addPoint(0, Tile.SIZE);
			b.translate(p.x * Tile.SIZE, p.y * Tile.SIZE);
			
			if (Assistant.intersection(b, line)) {
				dead = true;
				return;
			}
		}
		
		for (Entity e : Game.world.entities) {
			if (e instanceof Player && e.getBump(0, 0).intersectsLine(line)) {
				if (((Player) e).getUser().getUsername().equals(Game.user.getUsername())) Game.player.dealDamage(damage, this);
				dead = true;
				return;
			}
		}
		
		pos.add(dif);
		
		if (pos.equals(target)) dead = true;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isDead() {
		return dead;
	}
}
