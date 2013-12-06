package de.dakror.spamwars.game.weapon;

import java.awt.Point;
import java.awt.Rectangle;

import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.projectile.Projectile;
import de.dakror.spamwars.game.projectile.ProjectileType;

/**
 * @author Dakror
 */
public class AssauleRifle extends Weapon
{
	public AssauleRifle()
	{
		super(new Rectangle(157, 2170, 887, 275), new Point(900, 2276), new Point(401, 2396), FireMode.AUTO, 5, 35, 30, 300, 60 * 2);
		type = WeaponType.ASSAULT_RIFLE;
	}
	
	@Override
	public void update(int tick)
	{
		super.update(tick);
		exit = new Point(830, 2310);
		exit = new Point(exit.x - tex.x, exit.y - tex.y);
	}
	
	@Override
	protected Projectile getPojectile(Vector pos, Vector target)
	{
		return new Projectile(pos, target, Game.user.getUsername(), ProjectileType.ASSAULT_RIFLE);
	}
}
