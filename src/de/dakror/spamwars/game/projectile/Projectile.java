package de.dakror.spamwars.game.projectile;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class Projectile implements Drawable
{
	private Rectangle tex;
	private Vector pos, target;
	private float speed, damage, range;
	
	private boolean dead;
	
	private float rot;
	
	public Projectile(Rectangle tex, Vector pos, Vector target, float speed, float damage, float range)
	{
		this.tex = tex;
		this.speed = speed;
		this.damage = damage;
		this.pos = pos;
		this.target = target;
		
		Vector dif = target.clone().sub(pos);
		dif.setLength(range);
		this.target = pos.clone().add(dif);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (dead) return;
		
		Vector pos = this.pos.clone().add(new Vector(Game.world.x, Game.world.y));
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.rotate(rot, pos.x + tex.width / 2, pos.y + tex.height / 2);
		g.setTransform(at);
		
		Helper.drawImage(Game.getImage("weapon/projectiles.png"), (int) pos.x, (int) pos.y, tex.width, tex.height, tex.x, tex.y, tex.width, tex.height, g);
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick)
	{
		if (dead) return;
		
		Vector dif = target.clone().sub(pos);
		if (dif.getLength() > speed) dif.setLength(speed);
		
		rot = (float) Math.toRadians(dif.getAngleOnXAxis());
		
		Vector nextPos = pos.clone().add(dif);
		
		Point p = Game.world.getTile((int) nextPos.x, (int) nextPos.y);
		Tile tile = Tile.values()[Game.world.getTileIdAtPixel((int) nextPos.x, (int) nextPos.y)];
		
		if (tile.getBump() != null)
		{
			Rectangle b = (Rectangle) tile.getBump().clone();
			b.translate(p.x * Tile.SIZE, p.y * Tile.SIZE);
			if (b.intersectsLine(pos.x, pos.y, nextPos.x, nextPos.y))
			{
				dead = true;
				return;
			}
		}
		else if (tile.getLeftY() >= 0)
		{
			Polygon b = new Polygon();
			b.addPoint(0, tile.getLeftY());
			b.addPoint(Tile.SIZE, tile.getRightY());
			b.addPoint(Tile.SIZE, Tile.SIZE);
			b.addPoint(0, Tile.SIZE);
			b.translate(p.x * Tile.SIZE, p.y * Tile.SIZE);
			
			if (Assistant.intersection(b, new Line2D.Float(pos.x, pos.y, nextPos.x, nextPos.y)))
			{
				dead = true;
				return;
			}
		}
		
		pos.add(dif);
		
		if (pos.equals(target)) dead = true;
	}
	
	public float getRange()
	{
		return range;
	}
	
	public float getDamage()
	{
		return damage;
	}
	
	public boolean isDead()
	{
		return dead;
	}
}
