package de.dakror.spamwars.game.weapon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;


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
	
	public BufferedImage getIcon()
	{
		if (Game.getImage(class1.getName()) == null)
		{
			try
			{
				Weapon w = (Weapon) class1.getConstructor().newInstance();
				BufferedImage bi = w.getImage();
				Dimension size = Helper.getRelativeScaled(new Dimension(bi.getWidth(), bi.getHeight()), new Dimension(bi.getWidth(), bi.getHeight()), new Dimension(150, 20));
				
				Game.cacheImage(class1.getName(), Helper.toBufferedImage(bi.getScaledInstance(size.width, size.height, Image.SCALE_AREA_AVERAGING)));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return Game.getImage(class1.getName());
		
		
	}
}
