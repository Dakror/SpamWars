package de.dakror.spamwars.game.weapon;


/**
 * @author Dakror
 */
public enum WeaponType
{
	HANDGUN(Handgun.class),
	ASSAULT_RIFLE(AssauleRifle.class),
	
	;
	
	Class<?> class1;
	
	private WeaponType(Class<?> class1)
	{
		this.class1 = class1;
	}
	
	public Class<?> getClass1()
	{
		return class1;
	}
}
