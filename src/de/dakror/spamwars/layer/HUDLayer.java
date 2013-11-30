package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class HUDLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawContainer(Game.getWidth() / 2 - 200, Game.getHeight() - 50, 400, 60, false, false, g);
		Helper.drawProgressBar(Game.getWidth() / 2 - 180, Game.getHeight() - 30, 360, Game.player.getLife() / (float) Game.player.getMaxlife(), "ff3232", g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{}
}
