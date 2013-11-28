package de.dakror.spamwars.game.projectile;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class Projectile implements Drawable
{
	private Rectangle tex;
	private Vector pos, target;
	private float speed, damage;
	
	private boolean dead;
	
	private float rot;
	
	public Projectile(Rectangle tex, Vector pos, Vector target, float speed, float damage)
	{
		this.tex = tex;
		this.speed = speed;
		this.damage = damage;
		this.pos = pos;
		this.target = target;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (dead) return;
		
		Helper.drawImage(Game.getImage("weapon/projectiles.png"), (int) pos.x, (int) pos.y, tex.width, tex.height, tex.x, tex.y, tex.width, tex.height, g);
	}
	
	@Override
	public void update(int tick)
	{
		if (dead) return;
		
		Vector dif = target.clone().sub(pos);
		if (dif.getLength() > speed) dif.setLength(speed);
		
		rot = dif.getAngleOnXAxis();
		
		pos.add(dif);
		
		if (pos.equals(target)) dead = true;
	}
	
	public boolean isDead()
	{
		return dead;
	}
}
