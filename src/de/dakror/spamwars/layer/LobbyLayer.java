package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet04ServerInfo;
import de.dakror.spamwars.net.packet.Packet05World;
import de.dakror.spamwars.ui.ClickEvent;
import de.dakror.spamwars.ui.TextButton;

/**
 * @author Dakror
 */
public class LobbyLayer extends MPLayer
{
	User[] users;
	
	boolean fade;
	
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
		
		drawComponents(g);
		
		if (Game.currentGame.alpha == 1 && Game.world != null)
		{
			Game.world.render();
			Game.currentGame.setLayer(new HUDLayer());
			
			Game.currentGame.fadeTo(0, 0.05f);
		}
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void init()
	{
		if (!Game.client.isConnected()) // host
		{
			Game.server = new Server(Game.ip);
			
			TextButton start = new TextButton(Game.getWidth() / 2 + 50, Game.getHeight() / 4 * 3, 400, 80, "Spiel starten");
			start.addClickEvent(new ClickEvent()
			{
				@Override
				public void trigger()
				{
					Game.server.startGame();
				}
			});
			components.add(start);
			
			Game.client.connectToServer(Game.ip);
		}
		
		TextButton disco = new TextButton(Game.getWidth() / 2 - 450, Game.getHeight() / 4 * 3, 400, 80, "Trennen");
		disco.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.client.disconnect();
				
				if (Game.server != null)
				{
					Game.server.shutdown();
					Game.server = null;
				}
				
				Game.currentGame.removeLayer(LobbyLayer.this);
			}
		});
		components.add(disco);
		
		try
		{
			Game.client.sendPacket(new Packet04ServerInfo());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet04ServerInfo) users = ((Packet04ServerInfo) p).getUsers();
		if (p instanceof Packet05World) Game.currentGame.fadeTo(1, 0.05f);
	}
}
