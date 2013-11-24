package de.dakror.spamwars.game.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import de.dakror.spamwars.game.Game;


/**
 * @author Dakror
 */
public class Chunk
{
	public static final int SIZE = 8;
	
	int x, y;
	
	byte[][] data = new byte[SIZE][SIZE];
	
	BufferedImage image;
	
	public Chunk(int x, int y)
	{
		this.x = x;
		this.y = y;
		
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				data[i][j] = Tile.air.getId();
		
		image = new BufferedImage(SIZE * Tile.SIZE, SIZE * Tile.SIZE, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void render()
	{
		if (image == null) image = new BufferedImage(SIZE * Tile.SIZE, SIZE * Tile.SIZE, BufferedImage.TYPE_INT_ARGB);
		else image.flush();
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				Tile tile = Tile.getTileForId(data[i][j]);
				
				if (tile.equals(Tile.air)) continue;
				
				tile.drawTile(x, y, i, j, g);
			}
		}
	}
	
	public byte getTileId(int x, int y)
	{
		if (x < 0 || y < 0) return Tile.air.getId();
		
		if (x >= SIZE) x -= this.x * SIZE;
		if (y >= SIZE) y -= this.y * SIZE;
		
		return data[x][y];
	}
	
	public void draw(Graphics2D g)
	{
		if (image == null) render();
		
		g.drawImage(image, x * SIZE * Tile.SIZE, y * SIZE * Tile.SIZE, Game.w);
	}
	
	public void setTileId(int x, int y, byte d)
	{
		data[x][y] = d;
	}
	
	public byte[] getData()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				baos.write(data[i][j]);
			}
		}
		
		return baos.toByteArray();
	}
	
	public void setData(byte[] data)
	{
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				this.data[i][j] = data[i * SIZE + j];
			}
		}
	}
	
}
