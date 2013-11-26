package de.dakror.spamwars.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.EventListener;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.world.Tile;


/**
 * @author Dakror
 */
public abstract class Entity extends EventListener implements Drawable
{
	public float x, y;
	public int width, height;
	protected boolean gravity;
	protected boolean airborne;
	
	Vector velocity;
	
	protected Rectangle bump;
	
	public Entity(float x, float y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		velocity = new Vector(0, 0);
	}
	
	protected void drawBump(Graphics2D g)
	{
		Color o = g.getColor();
		g.setColor(Color.green);
		g.translate(x, y);
		g.draw(bump);
		g.translate(-x, -y);
		
		g.setColor(Color.yellow);
		g.draw(getTileSizeBump(0, 0));
		g.setColor(o);
	}
	
	protected abstract void tick(int tick);
	
	@Override
	public abstract void draw(Graphics2D g);
	
	public Rectangle getTileSizeBump(float tX, float tY)
	{
		Rectangle r = new Rectangle();
		r.x = (int) (Math.floor((bump.x + x + tX) / Tile.SIZE) * Tile.SIZE);
		r.y = (int) (Math.floor((bump.y + y + tY) / Tile.SIZE) * Tile.SIZE);
		
		int x2 = (int) (Math.ceil((bump.x + x + tX + bump.width) / Tile.SIZE) * Tile.SIZE);
		int y2 = (int) (Math.ceil((bump.y + y + tY + bump.height) / Tile.SIZE) * Tile.SIZE);
		
		r.width = x2 - r.x;
		r.height = y2 - r.y;
		
		return r;
	}
	
	public Rectangle getGridBump(float tX, float tY)
	{
		Rectangle r = getTileSizeBump(tX, tY);
		r.x /= Tile.SIZE;
		r.y /= Tile.SIZE;
		r.width /= Tile.SIZE;
		r.height /= Tile.SIZE;
		return r;
	}
	
	@Override
	public void update(int tick)
	{
		tick(tick);
		
		if (gravity) affectByGravity();
		
		float nx = x + velocity.x;
		float ny = y + velocity.y;
		
		boolean subs = false;
		
		Rectangle gridBump = getGridBump(velocity.x, velocity.y);
		
		for (int i = gridBump.x; i < gridBump.x + gridBump.width; i++)
		{
			for (int j = gridBump.y; j < gridBump.y + gridBump.height; j++)
			{
				Tile t = Tile.values()[Game.world.getTileId(i, j)];
				if (t.getBump() != null)
				{
					Rectangle r = (Rectangle) bump.clone();
					r.translate((int) nx, (int) ny);
					
					Rectangle b = (Rectangle) t.getBump().clone();
					b.translate(i * Tile.SIZE, j * Tile.SIZE);
					
					Rectangle is = r.intersection(b);
					
					subs = true;
					
					if (is.width > is.height) // vertical
					{
						if (is.height >= 0)
						{
							if (is.y != r.y) ny -= is.height;
							else ny += is.height;
						}
					}
					else
					{
						if (is.width >= 0)
						{
							if (is.x != r.x) nx -= is.width;
							else nx += is.width;
						}
					}
				}
			}
		}
		
		if (x == nx) velocity.x = 0;
		if (y == ny)
		{
			velocity.y = 0;
			if (subs) airborne = false;
		}
		
		x = nx;
		y = ny;
	}
	
	public void affectByGravity()
	{
		float g = 0.5f;
		
		velocity.y += g;
	}
	
	public Vector getPos()
	{
		return new Vector(x, y);
	}
}
