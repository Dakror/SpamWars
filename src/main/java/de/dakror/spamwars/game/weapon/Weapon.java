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


package de.dakror.spamwars.game.weapon;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.anim.Animation;
import de.dakror.spamwars.game.projectile.Projectile;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.layer.HUDLayer;
import de.dakror.spamwars.net.packet.Packet11GameInfo.GameMode;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Weapon implements Drawable {
	public static final float scale = 0.25f;
	protected WeaponData data;
	protected BufferedImage image;
	
	protected Point exit, grab;
	
	public float rot, rot2; // in degrees
	public boolean left, reloading;
	boolean overangle;
	
	int lastShot;
	
	public int capacity, magazine, capacityMax, ammo;
	
	private float x, y;
	
	Vector target;
	public boolean enabled;
	
	public Weapon(WeaponData data) {
		this.data = data;
		this.data.calculateStats();
		image = data.getImage();
		
		magazine = data.getMagazine();
		capacity = capacityMax = magazine * 5;
		ammo = data.getMagazine();
		enabled = false;
		
		grab = data.getGrab();
		exit = data.getExit();
		rot = 0;
		left = false;
	}
	
	public void target(Vector target) {
		if (!enabled) return;
		
		if (overangle) {
			this.target = null;
			return;
		}
		
		this.target = target;
		if (this.target != null) this.target.add(new Vector(Game.w.getInsets().left, Game.w.getInsets().top));
	}
	
	protected void shoot() {
		if (!enabled) return;
		
		Vector muzzle = getMuzzle();
		
		int mal = 30;
		
		Vector pos = new Vector(x + muzzle.x - Game.world.x, y + muzzle.y - Game.world.y);
		
		
		Point tile = Game.world.getTile((int) pos.x, (int) pos.y);
		Tile t = Tile.values()[Game.world.getTileIdAtPixel((int) pos.x, (int) pos.y)];
		
		if (t.getBump() != null) {
			Rectangle r = (Rectangle) t.getBump().clone();
			r.translate(tile.x * Tile.SIZE, tile.y * Tile.SIZE);
			if (r.contains(pos.x, pos.y)) return;
		}
		
		float rot3 = rot - (float) Math.toRadians(90);
		if (left) rot3 = (float) Math.toRadians(180) - rot3;
		
		if (target == null || reloading) return;
		
		if (ammo == 0) {
			if (CFG.AUTO_RELOAD && capacity > 0) {
				for (Layer l : Game.currentGame.layers) {
					if (l instanceof HUDLayer) {
						((HUDLayer) l).reload = true;
						((HUDLayer) l).reloadStarted = 0;
						break;
					}
				}
			}
			return;
		}
		
		float malus = (float) Math.random() * mal - mal / 2;
		
		Vector target = this.target.clone();
		
		target.y += malus;
		pos.y += malus;
		
		ammo--;
		Game.world.addAnimation(new Animation("muzzle", pos.clone().sub(new Vector(16 + (left ? 10 : 0), 16 + (left ? 10 : 0))), 1, rot3, 48, 23), true);
		Game.world.addProjectile(new Projectile(pos.clone(), target, Game.user.getUsername(), data.getProjectileSpeed(), data.getRange(), data.getDamage()), true);
	}
	
	@Override
	public void update(int tick) {
		if (Game.client.gameInfo.getGameMode() == GameMode.ONE_IN_THE_CHAMBER) {
			capacity = 0;
			magazine = 1;
			if (ammo > magazine) ammo = 1;
		}
		
		if (!enabled) return;
		
		if (target != null && (tick - lastShot) >= data.getSpeed()) {
			shoot();
			lastShot = tick;
			
			if (!data.isAutomatic()) target = null;
		}
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		
		x = (float) at.getTranslateX();
		y = (float) at.getTranslateY();
		
		at.scale(scale, scale * (left ? -1 : 1));
		double rot = rot2;
		
		double rotDeg = Math.toDegrees(rot);
		
		if (left) {
			if (180 - rotDeg > data.getAngle() && rotDeg >= 0) {
				rot = Math.toRadians(180 - data.getAngle());
				overangle = true;
			} else if (180 + rotDeg > data.getAngle() && rotDeg <= 0) {
				rot = Math.toRadians(180 + data.getAngle());
				overangle = true;
			} else overangle = false;
		} else {
			if (rotDeg < -data.getAngle()) {
				rot = Math.toRadians(-data.getAngle());
				overangle = true;
			} else if (rotDeg > data.getAngle()) {
				rot = Math.toRadians(data.getAngle());
				overangle = true;
			} else overangle = false;
		}
		
		this.rot = (float) rot;
		
		at.rotate(rot, 0, 0);
		g.setTransform(at);
		
		g.drawImage(image, -grab.x, -grab.y, Game.w);
		g.setTransform(old);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Vector getMuzzle() {
		Vector v = new Vector(exit).mul(scale).sub(new Vector(grab).mul(scale));
		
		float radius = (exit.x - grab.x + 50) * Weapon.scale;
		float rot2 = (float) (rot + Math.atan2(v.y, v.x)) * (left ? -1 : 1);
		
		return new Vector((float) Math.cos(rot2) * radius, (float) Math.sin(rot2) * radius);
	}
	
	public Point getExit() {
		return exit;
	}
	
	public Point getGrab() {
		return grab;
	}
	
	public boolean canReload() {
		if (capacity == 0) return false;
		if (ammo == magazine) return false;
		
		return true;
	}
	
	public void reload() {
		if (capacity == 0) return;
		
		int sub = capacity >= magazine ? magazine : capacity;
		ammo = sub;
		capacity -= sub;
	}
	
	public boolean canRefill() {
		return capacity < capacityMax;
	}
	
	public void refill(int amount) {
		capacity = capacity + amount > capacityMax ? capacityMax : capacity + amount;
	}
	
	public WeaponData getData() {
		return data;
	}
	
	public Vector getTarget() {
		return target;
	}
}
