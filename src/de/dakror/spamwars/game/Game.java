package de.dakror.spamwars.game;

import java.awt.Graphics2D;

import de.dakror.gamesetup.GameFrame;
import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Game extends GameFrame
{
	public World world;
	
	public Game()
	{
		super();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		world.draw(g);
	}
	
	@Override
	public void initGame()
	{
		world = new World(getClass().getResource("/map/map.txt"));
		world.render();
	}
}
