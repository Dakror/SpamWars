package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

public class ConnectingLayer extends Layer
{
	int tick;
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		String dots = "";
		for (int i = 0; i < (tick / 30) % 4; i++)
			dots += ".";
		Helper.drawHorizontallyCenteredString("Verbinde" + dots, Game.getWidth(), Game.getHeight() / 2, g, 60);
	}
	
	@Override
	public void update(int tick)
	{
		this.tick = tick;
	}
	
	@Override
	public void init()
	{
		modal = true;
	}
}
