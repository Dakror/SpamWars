package de.dakror.spamwars.game.projectile;

import java.awt.Rectangle;

/**
 * @author Dakror
 */
public enum ProjectileType
{
	DEFAUL_LEAD(new Rectangle(275, 89, 20, 10), 50, 5, 1000), ;
	
	private Rectangle tex;
	private float speed, damage, range;
	
	private ProjectileType(Rectangle tex, float speed, float damage, float range)
	{
		this.tex = tex;
		this.speed = speed;
		this.damage = damage;
		this.range = range;
	}
	
	public Rectangle getTex()
	{
		return tex;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public float getDamage()
	{
		return damage;
	}
	
	public float getRange()
	{
		return range;
	}
}
