package de.dakror.spamwars.game.weapon;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.projectile.Projectile;

/**
 * @author Dakror
 */
public abstract class Weapon implements Drawable
{
	public static final float scale = 0.25f;
	protected Rectangle tex;
	protected Point exit, grab;
	protected float damage;
	protected float maxAngle;
	
	public float rot; // in degrees
	public boolean left;
	
	private float x, y;
	
	public Weapon(Rectangle tex, Point exit, Point grab, float maxAngle)
	{
		this.tex = tex;
		this.exit = exit;
		this.maxAngle = maxAngle;
		this.grab = new Point(grab.x - tex.x, grab.y - tex.y);
		this.exit = new Point(exit.x - tex.x, exit.y - tex.y);
		rot = 0;
		left = false;
	}
	
	public void shoot(Vector target)
	{
		Vector muzzle = getMuzzle();
		Game.world.addProjectile(getPojectile(new Vector(x + muzzle.x - Game.world.x, y + muzzle.y - Game.world.y), target.sub(new Vector(Game.world.x, Game.world.y))));
	}
	
	protected abstract Projectile getPojectile(Vector pos, Vector target);
	
	@Override
	public void draw(Graphics2D g)
	{
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		
		x = (float) at.getTranslateX();
		y = (float) at.getTranslateY();
		
		at.scale(scale, scale * (left ? -1 : 1));
		double rot = this.rot;
		
		double rotDeg = Math.toDegrees(rot);
		
		if (left)
		{
			if (180 - rotDeg > maxAngle && rotDeg >= 0) rot = Math.toRadians(180 - maxAngle);
			if (180 + rotDeg > maxAngle && rotDeg <= 0) rot = Math.toRadians(180 + maxAngle);
		}
		else
		{
			if (rotDeg < -maxAngle) rot = Math.toRadians(-maxAngle);
			if (rotDeg > maxAngle) rot = Math.toRadians(maxAngle);
		}
		
		this.rot = (float) rot;
		
		at.rotate(rot, 0, 0);
		g.setTransform(at);
		
		Helper.drawImage(Game.getImage("weapon/show.png"), -grab.x, -grab.y, tex.width, tex.height, tex.x, tex.y, tex.width, tex.height, g);
		
		g.setTransform(old);
	}
	
	public Vector getMuzzle()
	{
		Vector v = new Vector(exit).mul(scale).sub(new Vector(grab).mul(scale));
		
		float radius = exit.x * Weapon.scale;
		float rot2 = (float) (rot + Math.atan2(v.y, v.x)) * (left ? -1 : 1);
		
		return new Vector((float) Math.cos(rot2) * radius, (float) Math.sin(rot2) * radius);
	}
	
	public Point getExit()
	{
		return exit;
	}
	
	public Point getGrab()
	{
		return grab;
	}
}
