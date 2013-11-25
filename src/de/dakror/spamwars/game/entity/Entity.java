package de.dakror.spamwars.game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.EventListener;
import de.dakror.gamesetup.util.Vector;


/**
 * @author Dakror
 */
public abstract class Entity extends EventListener implements Drawable
{
	public float x, y;
	public int width, height;
	
	protected Rectangle bump;
	
	public Entity(float x, float y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	protected void drawBump(Graphics2D g)
	{
		g.translate(x, y);
		g.draw(bump);
		g.translate(-x, -y);
	}
	
	protected abstract void tick(int tick);
	
	@Override
	public abstract void draw(Graphics2D g);
	
	@Override
	public void update(int tick)
	{
		tick(tick);
	}
	
	public Vector getPos()
	{
		return new Vector(x, y);
	}
}
