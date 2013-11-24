package de.dakror.spamwars.game.world;

import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Drawable;

/**
 * @author Dakror
 */
public class World implements Drawable
{
	public int x, y, width, height;
	
	public Chunk[][] chunks;
	
	public World(int width, int height)
	{
		x = y = 0;
		this.width = width;
		this.height = height;
		
		chunks = new Chunk[(int) Math.ceil(width / (float) (Chunk.SIZE * Tile.SIZE))][(int) Math.ceil(height / (float) (Chunk.SIZE * Tile.SIZE))];
		for (int i = 0; i < chunks.length; i++)
			for (int j = 0; j < chunks[0].length; j++)
				chunks[i][j] = new Chunk(i, j);
	}
	
	@Override
	public void draw(Graphics2D e)
	{}
	
	@Override
	public void update(int e)
	{}
}
