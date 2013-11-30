package de.dakror.spamwars.game.layer;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class LobbyLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/startGame.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Color c = g.getColor();
		g.setColor(Color.decode("#1c0d09"));
		if (Game.server != null)
		{
			for (int i = 0; i < Game.server.clients.size(); i++)
			{
				Helper.drawHorizontallyCenteredString(Game.server.clients.get(i).getUsername(), Game.getWidth(), 400 + i * 60, g, 60);
			}
		}
		g.setColor(c);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		if (!Game.client.isConnected())
		{
			Game.server = new Server(Game.ip);
			
			Game.client.connectToServer(Game.ip);
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
}
