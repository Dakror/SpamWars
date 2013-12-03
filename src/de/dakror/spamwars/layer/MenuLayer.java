package de.dakror.spamwars.layer;

import java.awt.Font;
import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.ui.ClickEvent;
import de.dakror.spamwars.ui.MenuButton;
import de.dakror.universion.UniVersion;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		if (!CFG.INTERNET)
		{
			Font old = g.getFont();
			g.setFont(new Font("", Font.PLAIN, 20));
			Helper.drawHorizontallyCenteredString("Offline-Modus: " + Game.user.getUsername() + " (" + Game.user.getIP().getHostAddress() + ")", Game.getWidth(), 20, g, 20);
			g.setFont(old);
		}
		
		Helper.drawString(UniVersion.prettyVersion(), 10, Game.getHeight() - 10, g, 18);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1 && enabled)
		{
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread()
			{
				@Override
				public void run()
				{
					Game.currentFrame.addLayer(new LobbyLayer());
				}
			}.start();
		}
		
		enabled = Game.currentGame.getActiveLayer().equals(this);
	}
	
	@Override
	public void init()
	{
		MenuButton start = new MenuButton("startGame", 0);
		start.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(start);
		MenuButton joingame = new MenuButton("joinGame", 1);
		joingame.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.addLayer(new JoinLayer());
			}
		});
		components.add(joingame);
		MenuButton opt = new MenuButton("options", 2);
		components.add(opt);
		MenuButton end = new MenuButton("endGame", 3);
		end.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				System.exit(0);
			}
		});
		components.add(end);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
}
