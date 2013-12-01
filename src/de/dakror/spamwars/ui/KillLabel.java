package de.dakror.spamwars.ui;

import java.awt.Graphics2D;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class KillLabel extends Component
{
	public KillLabel(int y)
	{
		super(Game.getWidth() - 380, y, 350, 64);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawShadow(x, y, width, height, g);
		Helper.drawOutline(x, y, width, height, false, g);
	}
	
	@Override
	public void update(int tick)
	{}
}
