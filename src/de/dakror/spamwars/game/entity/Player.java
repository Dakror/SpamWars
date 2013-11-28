package de.dakror.spamwars.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import de.dakror.spamwars.game.Game;


/**
 * @author Dakror
 */
public class Player extends Entity
{
	public boolean left, right, up, down;
	
	/**
	 * -1 = front, 0-10 = walking, 11 = jump
	 */
	int frame = -1;
	
	public Player(float x, float y)
	{
		super(x, y, 72, 97);
		
		bump = new Rectangle(10, 7, 44, 84);
		gravity = true;
	}
	
	@Override
	public void draw(Graphics2D g, float mapX, float mapY)
	{
		float mx = x + mapX;
		float my = y + mapY;
		
		if (frame == -1) g.drawImage(Game.getImage("entity/player/p1/p1_front.png"), (int) mx, (int) my, Game.w);
		else if (frame >= 0 && frame <= 10)
		{
			String frame = (this.frame + 1) + "";
			if (frame.length() == 1) frame = "0" + frame;
			
			AffineTransform old = g.getTransform();
			if (left && !right)
			{
				AffineTransform at = g.getTransform();
				at.translate((mx + width / 2) * 2, 0);
				at.scale(-1, 1);
				g.setTransform(at);
			}
			g.drawImage(Game.getImage("entity/player/p1/p1_walk" + frame + ".png"), (int) mx, (int) my, Game.w);
			
			g.setTransform(old);
		}
		else if (frame == 11)
		{
			AffineTransform old = g.getTransform();
			if (left && !right)
			{
				AffineTransform at = g.getTransform();
				at.translate((mx + width / 2) * 2, 0);
				at.scale(-1, 1);
				g.setTransform(at);
			}
			
			g.drawImage(Game.getImage("entity/player/p1/p1_jump.png"), (int) mx, (int) my, Game.w);
			
			g.setTransform(old);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_A:
			{
				left = true;
				break;
			}
			case KeyEvent.VK_D:
			{
				right = true;
				break;
			}
			case KeyEvent.VK_SPACE:
			{
				if (!airborne) velocity.y = -15;
				up = true;
				break;
			}
			// case KeyEvent.VK_SHIFT:
			// {
			// down = true;
			// break;
			// }
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_A:
			{
				left = false;
				break;
			}
			case KeyEvent.VK_D:
			{
				right = false;
				break;
			}
			case KeyEvent.VK_SPACE:
			{
				up = false;
				break;
			}
			// case KeyEvent.VK_S:
			// {
			// down = false;
			// break;
			// }
		}
	}
	
	@Override
	protected void tick(int tick)
	{
		int speed = airborne ? 6 : 8;
		
		if (left) velocity.x = -speed;
		if (right) velocity.x = speed;
		if (!airborne && velocity.x != 0 && tick % 2 == 0)
		{
			frame = frame < 0 ? 0 : frame;
			
			frame = (frame + 1) % 6;
		}
		else if (airborne)
		{
			frame = 11;
		}
		
		if (!left && !right)
		{
			frame = -1;
			velocity.x = 0;
		}
		
		int mx = (Game.getWidth() - width) / 2;
		int my = (Game.getHeight() - height) / 2;
		
		if (x > mx && Game.world.width - x > (Game.getWidth() + width) / 2) Game.world.x = -x + mx;
		if (y > my) Game.world.y = -y + my;
	}
}
