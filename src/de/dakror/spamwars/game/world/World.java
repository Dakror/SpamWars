package de.dakror.spamwars.game.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.util.Drawable;
import de.dakror.gamesetup.util.EventListener;
import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.anim.Animation;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.projectile.Projectile;
import de.dakror.spamwars.net.packet.Packet07Animation;
import de.dakror.spamwars.net.packet.Packet08Projectile;

/**
 * @author Dakror
 */
public class World extends EventListener implements Drawable
{
	public float x, y;
	public int width, height;
	
	public int[][] data;
	
	BufferedImage render;
	
	public CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Projectile> projectiles = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Animation> animations = new CopyOnWriteArrayList<>();
	
	URL file;
	
	ArrayList<Vector> spawns = new ArrayList<>();
	
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
	
	public World(int width, int height, byte[] data)
	{
		x = y = 0;
		this.width = width * Tile.SIZE;
		this.height = height * Tile.SIZE;
		this.data = new int[width][height];
		
		for (int i = 0; i < data.length; i++)
		{
			this.data[i / height][i % height] = data[i] + 128;
		}
	}
	
	public byte[] getData()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < width / Tile.SIZE; i++)
		{
			for (int j = 0; j < height / Tile.SIZE; j++)
			{
				baos.write(data[i][j] - 128);
			}
		}
		return baos.toByteArray();
	}
	
	public void render()
	{
		if (render == null) render = new BufferedImage(data.length * Tile.SIZE, data[0].length * Tile.SIZE, BufferedImage.TYPE_INT_ARGB);
		
		spawns = new ArrayList<>();
		
		render.flush();
		
		Graphics2D g = (Graphics2D) render.getGraphics();
		
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < data[0].length; j++)
			{
				if (data[i][j] == 0) continue;
				
				Tile t = Tile.values()[data[i][j]];
				
				if (t == Tile.boxCoinAlt)
				{
					// addEntity(new AmmoBox(i * Tile.SIZE, j * Tile.SIZE));
					continue;
				}
				
				if (t == Tile.bridge)
				{
					spawns.add(new Vector(i, j));
					continue;
				}
				
				g.drawImage(Game.getImage("tile/" + t.name() + ".png"), i * Tile.SIZE, j * Tile.SIZE, Tile.SIZE, Tile.SIZE, Game.w);
			}
		}
	}
	
	public Vector getBestSpawnPoint()
	{
		Vector point = null;
		float clostestPlayer = 0;
		
		for (Vector p : spawns)
		{
			float dist = 0;
			for (Entity e : entities)
			{
				if (e instanceof Player)
				{
					float d = e.getPos().sub(p.clone().mul(Tile.SIZE)).getLength();
					if (dist == 0 || d < dist) dist = d;
				}
			}
			if (dist > clostestPlayer || point == null)
			{
				point = p;
				clostestPlayer = dist;
			}
		}
		
		return point;
	}
	
	public int getTileId(int x, int y)
	{
		if (y < 0 || y >= data[0].length) return Tile.air.ordinal();
		if (x < 0 || x >= data.length) return Tile.stone.ordinal();
		
		return data[x][y];
	}
	
	public boolean isFreeOfBumps(Rectangle grid)
	{
		for (int i = grid.x; i < grid.x + grid.width; i++)
		{
			for (int j = grid.y; j < grid.y + grid.height; j++)
			{
				Tile t = Tile.values()[getTileId(i, j)];
				if (t.getBump() != null)
				{
					return false;
				}
			}
		}
		
		return true;
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
	
	public int getTileIdAtPixel(int x, int y)
	{
		Point p = getTile(x, y);
		return getTileId(p.x, p.y);
	}
	
	public Point getTile(int x, int y)
	{
		return new Point((int) Math.floor(x / (float) Tile.SIZE), (int) Math.floor(y / (float) Tile.SIZE));
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
				if (t.getLeftY() >= 0)
				{
					Polygon p = new Polygon();
					p.addPoint(0, t.getLeftY());
					p.addPoint(Tile.SIZE, t.getRightY());
					p.addPoint(Tile.SIZE, Tile.SIZE);
					p.addPoint(0, Tile.SIZE);
					p.translate(i * Tile.SIZE, j * Tile.SIZE);
					
					area.add(new Area(p));
				}
			}
		}
		area.intersect(new Area(bump));
		
		return area;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (render == null) return;
		
		g.drawImage(render, (int) x, (int) y, Game.w);
		
		for (Entity e : entities)
			e.draw(g);
		
		for (Projectile e : projectiles)
			e.draw(g);
		
		for (Animation a : animations)
			a.draw(g);
	}
	
	@Override
	public void update(int tick)
	{
		for (Entity e : entities)
			e.update(tick);
		
		for (Projectile p : projectiles)
		{
			p.update(tick);
			if (p.isDead()) projectiles.remove(p);
		}
		
		for (Animation a : animations)
		{
			a.update(tick);
			if (a.isDead()) animations.remove(a);
		}
	}
	
	public void addEntity(Entity e)
	{
		entities.add(e);
	}
	
	public void addProjectile(Projectile p, boolean send)
	{
		projectiles.add(p);
		
		if (send)
		{
			try
			{
				Game.client.sendPacket(new Packet08Projectile(p));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void addAnimation(Animation a, boolean send)
	{
		animations.add(a);
		
		try
		{
			if (send) Game.client.sendPacket(new Packet07Animation(a));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		e.translatePoint(-(int) x, -(int) y);
		
		for (Entity e1 : entities)
			e1.mouseMoved(e);
		
		e.translatePoint((int) x, (int) y);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		e.translatePoint(-(int) x, -(int) y);
		
		for (Entity e1 : entities)
			e1.mousePressed(e);
		
		e.translatePoint((int) x, (int) y);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		e.translatePoint(-(int) x, -(int) y);
		
		for (Entity e1 : entities)
			e1.mouseReleased(e);
		
		e.translatePoint((int) x, (int) y);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		e.translatePoint(-(int) x, -(int) y);
		
		for (Entity e1 : entities)
			e1.mouseDragged(e);
		
		e.translatePoint((int) x, (int) y);
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
