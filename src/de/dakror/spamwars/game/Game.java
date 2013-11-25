package de.dakror.spamwars.game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import de.dakror.gamesetup.GameFrame;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Game extends GameFrame
{
	public World world;
	public static Game currentGame;
	
	
	public Game()
	{
		super();
		currentGame = this;
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
		
		world.addEntity(new Player(300, 538));
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		world.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		super.keyReleased(e);
		world.keyReleased(e);
	}
}
