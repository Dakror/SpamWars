package de.dakror.spamwars.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
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
		
		Color old = g.getColor();
		g.setColor(Color.green);
		
		Helper.drawString(getFPS() + " FPS", 0, 18, g, 18);
		Helper.drawString(getUPS() + " UPS", 100, 18, g, 18);
		
		g.setColor(old);
	}
	
	@Override
	public void initGame()
	{
		w.addWindowFocusListener(this);
		
		world = new World(getClass().getResource("/map/map.txt"));
		world.render();
		
		w.setBackground(Color.decode("#D0F4F7"));
		
		Player p = new Player(0, 0);
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
