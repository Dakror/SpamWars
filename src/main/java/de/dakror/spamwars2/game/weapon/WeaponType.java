package de.dakror.spamwars.game.weapon;

/**
 * @author Dakror
 */
public enum WeaponType
{
	WEAPON,
	FALL_DAMAGE,
	STOMP;
	
	public static String getMessage(WeaponType type)
	{
		switch (type)
		{
			case FALL_DAMAGE:
				return "%dead% landete nicht weich";
			case STOMP:
				return "%killer% zerstampfte %dead%";
			default:
				return null;
		}
	}
}
