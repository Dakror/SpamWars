package de.dakror.spamwars.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class RespawnLayer extends MPLayer
{
	public static final int RESPAWN_TIME = 5000; // ms
	
	long end;
	
	public RespawnLayer()
	{
		modal = false;
		end = 0;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (end == 0)
		{
			end = System.currentTimeMillis() + RESPAWN_TIME;
		}
		
		Composite oc = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		Color o = g.getColor();
		g.setColor(Color.gray);
		g.fillRect(0, 0, Game.getWidth(), Game.getHeight());
		g.setColor(o);
		g.setComposite(oc);
		
		if (end > 0)
		{
			Helper.drawHorizontallyCenteredString("RESPAWN in ", Game.getWidth(), Game.getHeight() / 3, g, 70);
			Helper.drawString((Math.round((end - System.currentTimeMillis()) / 100f) / 10f) + "", Game.getWidth() / 2 - 100, Game.getHeight() / 2, g, 90);
		}
		else
		{
			Helper.drawHorizontallyCenteredString("Du bist tot", Game.getWidth(), Game.getHeight() / 3, g, 90);
			Helper.drawHorizontallyCenteredString("Respawn nächste Runde", Game.getWidth(), Game.getHeight() / 3 + 100, g, 40);
		}
	}
	
	@Override
	public void update(int tick)
	{
		if (System.currentTimeMillis() >= end && end > 0)
		{
			Game.currentGame.removeLayer(this);
			Game.player.revive();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		for (Layer l : Game.currentGame.layers)
		{
			if (l instanceof HUDLayer)
			{
				((HUDLayer) l).onPacketReceived(p);
				break;
			}
		}
	}
	
	@Override
	public void init()
	{}
}
