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
	
	public AmmoBox(float x, float y)
	{
		super(x, y, Tile.SIZE, Tile.SIZE);
		
		bump = new Rectangle(0, 0, 1, 1);
	}
	
	@Override
	protected void tick(int tick)
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("tile/boxCoinAlt.png"), (int) (x + Game.world.x), (int) (y + Game.world.y), width, height, Game.w);
	}
}
