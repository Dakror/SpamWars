package de.dakror.spamwars.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Handgun;
import de.dakror.spamwars.game.weapon.Weapon;


/**
 * @author Dakror
 */
public class Player extends Entity
{
	public boolean left, right, up, down;
	
	boolean lookingLeft = false;
	
	Weapon weapon;
	
	/**
	 * 0 stand, 0-10 = walking, 11 = jump
	 */
	int frame = 0;
	
	Point hand = new Point(0, 0);
	
	Point mouse = new Point(0, 0);
	
	public Player(float x, float y)
	{
		super(x, y, 72, 97);
		
		bump = new Rectangle(10, 7, 44, 84);
		gravity = true;
		
		weapon = new Handgun();
	}
	
	@Override
	public void draw(Graphics2D g, float mapX, float mapY)
	{
		float mx = x + mapX;
		float my = y + mapY;
		
		AffineTransform old = g.getTransform();
		if (lookingLeft)
		{
			AffineTransform at = g.getTransform();
			at.translate((mx + width / 2) * 2, 0);
			at.scale(-1, 1);
			g.setTransform(at);
		}
		
		if (frame >= 0 && frame <= 10)
		{
			String frame = (this.frame + 1) + "";
			if (frame.length() == 1) frame = "0" + frame;
			
			g.drawImage(Game.getImage("entity/player/p1/p1_walk" + frame + ".png"), (int) mx, (int) my, Game.w);
		}
		else if (frame == 11)
		{
			g.drawImage(Game.getImage("entity/player/p1/p1_jump.png"), (int) mx, (int) my, Game.w);
		}
		g.setTransform(old);
		
		Color o = g.getColor();
		g.setColor(Color.red);
		g.fillRect(hand.x + (int) mx - 1, hand.y + (int) my - 1, 3, 3);
		g.setColor(o);
		
		old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(hand.x + mx, hand.y + my);
		g.setTransform(at);
		
		weapon.draw(g);
		
		g.setTransform(old);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		lookingLeft = e.getX() < x + width / 2;
		mouse = e.getPoint();
		
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		weapon.shoot(new Vector(e.getPoint()));
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
			frame = 3;
			velocity.x = 0;
		}
		
		int mx = (Game.getWidth() - width) / 2;
		int my = (Game.getHeight() - height) / 2;
		
		if (x > mx && Game.world.width - x > (Game.getWidth() + width) / 2) Game.world.x = -x + mx;
		if (y > my) Game.world.y = -y + my;
		
		weapon.left = lookingLeft;
		if (lookingLeft) hand = new Point(0, 60);
		else hand = new Point(65, 60);
		
	}
}
