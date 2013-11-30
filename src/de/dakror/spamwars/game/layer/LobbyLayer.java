package de.dakror.spamwars.game.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet3ServerInfo;

/**
 * @author Dakror
 */
public class LobbyLayer extends MPLayer
{
	User[] users;
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/startGame.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Color c = g.getColor();
		g.setColor(Color.decode("#1c0d09"));
		if (users != null)
		{
			for (int i = 0; i < users.length; i++)
			{
				Helper.drawHorizontallyCenteredString(users[i].getUsername(), Game.getWidth(), 400 + i * 60, g, 60);
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
		
		try
		{
			Game.client.sendPacket(new Packet3ServerInfo());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet3ServerInfo) users = ((Packet3ServerInfo) p).getUsers();
	}
}
