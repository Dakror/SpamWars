package de.dakror.spamwars.game.weapon;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part.Category;

/**
 * @author Dakror
 */
public class WeaponData {
	ArrayList<DataPart> parts;
	
	int speed, magazine, reload, angle, projectileSpeed, range, damage;
	
	boolean automatic;
	
	public WeaponData() {
		parts = new ArrayList<>();
		automatic = false;
	}
	
	public void addPart(Part p, double x, double y) {
		parts.add(new DataPart(p, x, y));
	}
	
	public void addPart(DataPart dp) {
		parts.add(dp);
	}
	
	public int getPrice() {
		int price = 0;
		
		for (DataPart p : parts)
			price += p.part.price;
		
		if (automatic) price += 500;
		
		return price;
	}
	
	@Override
	public String toString() {
		String s = "";
		for (DataPart p : parts) {
			s += p.toString() + ";";
		}
		
		s += Boolean.toString(automatic);
		
		return s;
	}
	
	public DataPart getPart(Category category) {
		for (DataPart p : parts) {
			if (p.part.category == category) return p;
		}
		
		return null;
	}
	
	/**
	 * Call after any modification to data is done!
	 */
	public WeaponData getSortedData() {
		WeaponData wd = new WeaponData();
		double minX = -1337, minY = -1337;
		
		for (DataPart p : parts) {
			if (minX == -1337 || p.x < minX) minX = p.x;
			if (minY == -1337 || p.y < minY) minY = p.y;
		}
		
		for (DataPart p : parts) {
			p.x -= minX;
			p.y -= minY;
			wd.addPart(p.part, p.x, p.y);
		}
		
		wd.automatic = automatic;
		
		return wd;
	}
	
	public void calculateStats() {
		int speed = 0, magazine = 0, reload = 0, angle = 0, projectileSpeed = 0, range = 0, damage = 0;
		
		for (DataPart p : parts) {
			speed += p.part.speed;
			magazine += p.part.magazine;
			reload += p.part.reload;
			angle += p.part.angle;
			projectileSpeed += p.part.projectileSpeed;
			range += p.part.range;
			damage += p.part.damage;
		}
		
		float c = parts.size();
		
		this.speed = Math.round(speed / c);
		this.magazine = Math.round(magazine / c);
		this.reload = Math.round(reload / c);
		this.angle = Math.round(angle / c);
		this.projectileSpeed = Math.round(projectileSpeed / c);
		this.range = Math.round(range / c);
		this.damage = Math.round(damage / c);
	}
	
	public DataPart getOrigin() {
		for (DataPart p : parts) {
			if (p.x == 0 && p.y == 0) return p;
		}
		
		return null;
	}
	
	public Point getGrab() {
		for (DataPart p : parts) {
			if (p.part.category == Category.HANDLE) return new Point((int) p.x + p.part.tex.width / 2, (int) p.y + p.part.tex.height / 2);
		}
		
		return null;
	}
	
	public Point getExit() {
		for (DataPart p : parts) {
			if (p.part.category == Category.BARREL) return new Point((int) p.x + p.part.tex.width, (int) p.y + p.part.tex.height / 2);
		}
		
		return null;
	}
	
	public BufferedImage getImage() {
		int width = 0, height = 0;
		for (DataPart p : parts) {
			if (p.x + p.part.tex.width > width) width = (int) (p.x + p.part.tex.width);
			if (p.y + p.part.tex.height > height) height = (int) (p.y + p.part.tex.height);
		}
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		for (DataPart p : parts) {
			Helper.drawImage2(Game.getImage("weapon/explode.png"), (int) p.x, (int) p.y, p.part.tex.width, p.part.tex.height, p.part.tex.x, p.part.tex.y, p.part.tex.width,
												p.part.tex.height, g);
		}
		
		return bi;
	}
	
	public ArrayList<DataPart> getParts() {
		return parts;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getMagazine() {
		return magazine;
	}
	
	public int getReload() {
		return reload;
	}
	
	public int getAngle() {
		return angle;
	}
	
	public int getProjectileSpeed() {
		return projectileSpeed;
	}
	
	public int getRange() {
		return range;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public boolean isAutomatic() {
		return automatic;
	}
	
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WeaponData) {
			return toString().equals(obj.toString());
		}
		
		return false;
	}
	
	public static WeaponData load(String s) {
		WeaponData wd = new WeaponData();
		
		String[] parts = s.split(";");
		
		for (int i = 0; i < parts.length - 1; i++) {
			wd.addPart(DataPart.load(parts[i]));
		}
		
		wd.automatic = parts[parts.length - 1].equals("true");
		
		return wd;
	}
}
