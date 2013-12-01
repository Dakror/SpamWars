package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.KillLabel;

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
		Font old = g.getFont();
		g.setFont(new Font("Arial", Font.PLAIN, 25));
		Color o = g.getColor();
		g.setColor(Color.black);
		Helper.drawHorizontallyCenteredString(Game.player.getLife() + " / " + Game.player.getMaxlife(), Game.getWidth(), Game.getHeight() - 14, g, 14);
		g.setFont(old);
		g.setColor(o);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{
		components.add(new KillLabel(50));
	}
}
