package de.dakror.spamwars.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.world.Tile;

/**
 * @author Dakror
 */
public class AmmoBox extends Entity
{
	public static final int AMMO = 50;
	public static final int TIMEOUT = 60 * 60; // 60 secs
	int tick;
	int startTick;
	int random;
	
	public AmmoBox(float x, float y)
	{
		super(x, y, Tile.SIZE, Tile.SIZE);
		update = false;
		bump = new Rectangle(0, 0, Tile.SIZE, Tile.SIZE);
		random = (int) (Math.random() * 1000);
	}
	
	@Override
	protected void tick(int tick)
	{
		this.tick = tick;
		if (!isEnabled())
		{
			if (startTick == 0) startTick = tick;
			
			if (tick - startTick >= TIMEOUT) setEnabled(true, true);
		}
		else startTick = 0;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("tile/boxCoinAlt.png"), (int) (x + Game.world.x) + 10, (int) (y + Game.world.y + 5 * Math.sin((tick + random) / 13f) + 10), width - 20, height - 20, Game.w);
	}
}
