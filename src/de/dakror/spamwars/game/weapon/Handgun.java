package de.dakror.spamwars.game.weapon;

import java.awt.Point;
import java.awt.Rectangle;

import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.projectile.Projectile;
import de.dakror.spamwars.game.projectile.ProjectileType;

/**
 * @author Dakror
 */
public class Handgun extends Weapon
{
	public Handgun()
	{
		super(new Rectangle(629, 1125, 212, 129), new Point(841, 1147), new Point(659, 1212), 50);
		damage = 4;
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	protected Projectile getPojectile(Vector pos, Vector target)
	{
		return new Projectile(pos, target, ProjectileType.HANDGUN);
	}
}
