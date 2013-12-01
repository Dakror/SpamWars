package de.dakror.spamwars.game.weapon;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.anim.Animation;
import de.dakror.spamwars.game.projectile.Projectile;

/**
 * @author Dakror
 */
public abstract class Weapon implements Drawable
{
	public enum FireMode
	{
		SINGLE,
		AUTO
	}
	
	public static final float scale = 0.25f;
	protected Rectangle tex;
	protected Point exit, grab;
	protected float maxAngle;
	
	public float rot, rot2; // in degrees
	public boolean left;
	boolean overangle;
	
	public WeaponType type;
	public FireMode fireMode;
	
	int lastShot;
	
	public int speed;
	
	private float x, y;
	
	Vector target;
	
	public Weapon(Rectangle tex, Point exit, Point grab, FireMode fireMode, int speed, float maxAngle)
	{
		this.tex = tex;
		this.exit = exit;
		this.fireMode = fireMode;
		this.maxAngle = maxAngle;
		this.speed = speed;
		this.grab = new Point(grab.x - tex.x, grab.y - tex.y);
		this.exit = new Point(exit.x - tex.x, exit.y - tex.y);
		rot = 0;
		left = false;
	}
	
	public void target(Vector target)
	{
		if (overangle) return;
		
		this.target = target;
	}
	
	protected void shoot()
	{
		if (target == null) return;
		Vector muzzle = getMuzzle();
		
		Vector pos = new Vector(x + muzzle.x - Game.world.x, y + muzzle.y - Game.world.y);
		
		float rot3 = rot - (float) Math.toRadians(90);
		if (left) rot3 = (float) Math.toRadians(180) - rot3;
		
		Game.world.addAnimation(new Animation("muzzle", pos.clone().sub(new Vector(16 + (left ? 10 : 0), 16 + (left ? 10 : 0))), 1, rot3, 48, 23), true);
		
		Game.world.addProjectile(getPojectile(pos.clone(), target), true);
	}
	
	protected abstract Projectile getPojectile(Vector pos, Vector target);
	
	@Override
	public void update(int tick)
	{
		if (target != null && (tick - lastShot) >= speed)
		{
			shoot();
			lastShot = tick;
			
			if (fireMode == FireMode.SINGLE) target = null;
		}
		
		if (target == null) lastShot = 0;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		AffineTransform old = g.getTransform();
		AffineTransform at = g.getTransform();
		
		x = (float) at.getTranslateX();
		y = (float) at.getTranslateY();
		
		at.scale(scale, scale * (left ? -1 : 1));
		double rot = rot2;
		
		double rotDeg = Math.toDegrees(rot);
		
		if (left)
		{
			if (180 - rotDeg > maxAngle && rotDeg >= 0)
			{
				rot = Math.toRadians(180 - maxAngle);
				overangle = true;
			}
			else if (180 + rotDeg > maxAngle && rotDeg <= 0)
			{
				rot = Math.toRadians(180 + maxAngle);
				overangle = true;
			}
			else overangle = false;
		}
		else
		{
			if (rotDeg < -maxAngle)
			{
				rot = Math.toRadians(-maxAngle);
				overangle = true;
			}
			else if (rotDeg > maxAngle)
			{
				rot = Math.toRadians(maxAngle);
				overangle = true;
			}
			else overangle = false;
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
