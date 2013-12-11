package de.dakror.spamwars.game.weapon;

import de.dakror.spamwars.game.weapon.Weapon.FireMode;

/**
 * @author Dakror
 */
public class WeaponProperties
{
	int speed, magazine, capacity, reload;
	float angle;
	FireMode mode;
	
	public WeaponProperties(int speed, float angle, int magazine, int capacity, int reload, FireMode mode)
	{
		this.speed = speed;
		this.angle = angle;
		this.magazine = magazine;
		this.capacity = capacity;
		this.reload = reload;
		this.mode = mode;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public int getMagazine()
	{
		return magazine;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public int getReload()
	{
		return reload;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	public FireMode getMode()
	{
		return mode;
	}
}
