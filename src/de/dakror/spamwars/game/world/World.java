package de.dakror.spamwars.game.world;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.EventListener;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;

/**
 * @author Dakror
 */
public class World extends EventListener implements Drawable
{
	public int x, y, width, height;
	
	public int[][] data;
	
	BufferedImage render;
	
	CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
	
	public World(int width, int height)
	{
		x = y = 0;
		this.width = width;
		this.height = height;
		
		data = new int[(int) Math.ceil(width / (float) Tile.SIZE)][(int) Math.ceil(height / (float) Tile.SIZE)];
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				data[i][j] = Tile.air.ordinal();
	}
	
	public World(URL worldFile)
	{
		try
		{
			x = y = 0;
			String content = Helper.getURLContent(worldFile);
			width = Integer.parseInt(content.substring(0, content.indexOf(":"))) * Tile.SIZE;
			height = Integer.parseInt(content.substring(content.indexOf(":") + 1, content.indexOf(";"))) * Tile.SIZE;
			String[] raw = content.substring(content.indexOf(";") + 1).split(";");
			
			data = new int[width / Tile.SIZE][height / Tile.SIZE];
			for (int i = 0; i < data.length; i++)
			{
				for (int j = 0; j < data[0].length; j++)
				{
					data[i][j] = Integer.parseInt(raw[j * data.length + i]);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void render()
	{
		if (render == null) render = new BufferedImage(data.length * Tile.SIZE, data[0].length * Tile.SIZE, BufferedImage.TYPE_INT_ARGB);
		
		render.flush();
		
		Graphics2D g = (Graphics2D) render.getGraphics();
		
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < data[0].length; j++)
			{
				if (data[i][j] == 0) continue;
				
				Tile t = Tile.values()[data[i][j]];
				
				g.drawImage(Game.getImage("tile/" + t.name() + ".png"), i * Tile.SIZE, j * Tile.SIZE, Tile.SIZE, Tile.SIZE, Game.w);
				
				if (t.getBump() != null)
				{
					g.translate(i * Tile.SIZE, j * Tile.SIZE);
					g.draw(t.getBump());
					g.translate(-i * Tile.SIZE, -j * Tile.SIZE);
				}
				if (t.getLeftY() >= 0)
				{
					g.drawLine(i * Tile.SIZE, j * Tile.SIZE + t.getLeftY(), i * Tile.SIZE + Tile.SIZE, j * Tile.SIZE + t.getRightY());
				}
			}
		}
	}
	
	public int getTileId(int x, int y)
	{
		if (x < 0 || y < 0 || x >= data.length || y >= data[0].length) return Tile.air.ordinal(); // outside of world = air
		
		return data[x][y];
	}
	
	public boolean isFree(Rectangle grid)
	{
		for (int i = grid.x; i < grid.x + grid.width; i++)
		{
			for (int j = grid.y; j < grid.y + grid.height; j++)
			{
				Tile t = Tile.values()[getTileId(i, j)];
				if (t.getBump() != null || t.getLeftY() >= 0)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean intersects(Rectangle grid, Rectangle bump)
	{
		return !intersection(grid, bump).isEmpty();
	}
	
	public Area intersection(Rectangle grid, Rectangle bump)
	{
		Area area = new Area();
		for (int i = grid.x; i < grid.x + grid.width; i++)
		{
			for (int j = grid.y; j < grid.y + grid.height; j++)
			{
				Tile t = Tile.values()[getTileId(i, j)];
				if (t.getBump() != null)
				{
					Rectangle r = (Rectangle) t.getBump().clone();
					r.translate(i * Tile.SIZE, j * Tile.SIZE);
					area.add(new Area(r));
				}
			}
		}
		area.intersect(new Area(bump));
		
		return area;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(render, x, y, Game.w);
		
		for (Entity e : entities)
			e.draw(g);
	}
	
	@Override
	public void update(int tick)
	{
		for (Entity e : entities)
			e.update(tick);
	}
	
	public void addEntity(Entity e)
	{
		entities.add(e);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		for (Entity e1 : entities)
			e1.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		for (Entity e1 : entities)
			e1.keyReleased(e);
	}
}
