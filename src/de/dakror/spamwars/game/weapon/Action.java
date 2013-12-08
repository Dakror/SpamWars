package de.dakror.spamwars.game.weapon;

/**
 * @author Dakror
 */
public class Action
{
	public WeaponType type;
	public String username;
	
	public Action(WeaponType type, String username)
	{
		this.type = type;
		this.username = username;
	}
}
