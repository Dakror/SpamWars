package de.dakror.spamwars.game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import de.dakror.gamesetup.GameFrame;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.world.World;

/**
 * @author Dakror
 */
public class Game extends GameFrame implements WindowFocusListener
{
	public static World world;
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
		w.addWindowFocusListener(this);
		
		world = new World(getClass().getResource("/map/map.txt"));
		world.render();
		
		
		Player p = new Player(300, 538 - 200);
		world.player = p;
		
		world.addEntity(p);
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
	
	@Override
	public void windowGainedFocus(WindowEvent e)
	{}
	
	@Override
	public void windowLostFocus(WindowEvent e)
	{
		world.player.left = false;
		world.player.right = false;
		world.player.up = false;
		world.player.down = false;
	}
	
	
}
