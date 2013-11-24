package de.dakror.spamwars.game.world;

import java.awt.Graphics2D;

import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class Tile
{
	public static final int SIZE = 70;
	public static final int TILES = 256;
	private static Tile[] tileList = new Tile[TILES];
	
	public static Tile air = new Tile(0, "Luft", null);
	
	protected String name = "Unnamed";
	protected String tileset;
	protected byte id;
	
	public Tile(int id, String name, String tileset)
	{
		register(id);
		
		this.name = name;
		this.tileset = tileset;
	}
	
	private void register(int id)
	{
		if (tileList[id] == null) tileList[id] = this;
		this.id = (byte) (id - 128);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTileset()
	{
		return tileset;
	}
	
	public byte getId()
	{
		return id;
	}
	
	public void drawTile(int cx, int cy, int i, int j, Graphics2D g)
	{
		if (tileset == null) return;
		
		g.drawImage(Game.getImage("tile/" + tileset + ".png"), i * SIZE, j * SIZE, Game.w);
	}
	
	public static Tile getTileForId(byte id)
	{
		return tileList[id + 128];
	}
	
	public static Tile getTileForId(int id)
	{
		return tileList[id];
	}
}
