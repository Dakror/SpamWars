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
public class Projectile implements Drawable
{
	private Vector pos, target;
	ProjectileType type;
	
	private boolean dead;
	
	private float rot;
	
	public Projectile(Vector pos, Vector target, ProjectileType type)
	{
		this.type = type;
		this.pos = pos;
		this.target = target;
		
		Vector dif = target.clone().sub(pos);
		dif.setLength(type.getRange());
		this.target = pos.clone().add(dif);
	}
	
	public Projectile(JSONObject o) throws JSONException
	{
		this(new Vector((float) o.getDouble("x"), (float) o.getDouble("y")), new Vector((float) o.getDouble("tx"), (float) o.getDouble("ty")), ProjectileType.values()[o.getInt("t")]);
	}
	
	public JSONObject serialize()
	{
		JSONObject o = new JSONObject();
		try
		{
			o.put("x", pos.x);
			o.put("y", pos.y);
			o.put("tx", target.x);
			o.put("ty", target.y);
			o.put("t", type.ordinal());
			o.put("d", dead);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return o;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (dead) return;
		
		Vector pos = this.pos.clone().add(new Vector(Game.world.x, Game.world.y));
		
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(-Game.w.getInsets().left, -Game.w.getInsets().top);
		at.rotate(rot, pos.x + type.getTex().width / 2, pos.y + type.getTex().height / 2);
		g.setTransform(at);
		
		Helper.drawImage(Game.getImage("weapon/projectiles.png"), (int) pos.x, (int) pos.y, type.getTex().width, type.getTex().height, type.getTex().x, type.getTex().y, type.getTex().width, type.getTex().height, g);
		
		g.setTransform(old);
	}
	
	@Override
	public void update(int tick)
	{
		if (dead) return;
		
		Vector dif = target.clone().sub(pos);
		if (dif.getLength() > type.getSpeed()) dif.setLength(type.getSpeed());
		
		rot = (float) Math.toRadians(dif.getAngleOnXAxis());
		
		Vector nextPos = pos.clone().add(dif);
		
		Point p = Game.world.getTile((int) nextPos.x, (int) nextPos.y);
		Tile tile = Tile.values()[Game.world.getTileIdAtPixel((int) nextPos.x, (int) nextPos.y)];
		
		Line2D line = new Line2D.Float(pos.x, pos.y, nextPos.x, nextPos.y);
		
		if (tile.getBump() != null)
		{
			Rectangle b = (Rectangle) tile.getBump().clone();
			b.translate(p.x * Tile.SIZE, p.y * Tile.SIZE);
			if (b.intersectsLine(line))
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
			
			if (Assistant.intersection(b, line))
			{
				dead = true;
				return;
			}
		}
		
		for (Entity e : Game.world.entities)
		{
			if (e instanceof Player && e.getBump(0, 0).intersectsLine(line))
			{
				if (((Player) e).getUser().getUsername().equals(Game.user.getUsername())) Game.player.dealDamage(type.getDamage());
				dead = true;
				return;
			}
		}
		
		pos.add(dif);
		
		if (pos.equals(target)) dead = true;
	}
	
	public ProjectileType getType()
	{
		return type;
	}
	
	public boolean isDead()
	{
		return dead;
	}
}
