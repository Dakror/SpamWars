package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class MenuLayer extends Layer
{
	public static boolean waiting = true;
	
	@Override
	public void init()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawString("Version " + DakrorBin.buildDate, 10, Game.getHeight() - 10, g, 18);
		drawComponents(g);
		if (waiting)
		{
			drawModality(g);
			Helper.drawHorizontallyCenteredString("Verbinde...", Game.getWidth(), Game.getHeight() / 2, g, 60);
		}
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
}
