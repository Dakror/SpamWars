package de.dakror.spamwars.layer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.net.InetAddress;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer
{
	int goingto;
	
	static LobbyLayer ll;
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		if (!CFG.INTERNET && Game.user.getIP() != null)
		{
			Font old = g.getFont();
			g.setFont(new Font("", Font.PLAIN, 20));
			Helper.drawHorizontallyCenteredString("Offline-Modus: " + Game.user.getUsername() + " (" + Game.user.getIP().getHostAddress() + ")", Game.getWidth(), 20, g, 20);
			g.setFont(old);
		}
		
		Helper.drawString("Spam Wars " + DakrorBin.buildDate, 10, Game.getHeight() - 10, g, 18);
		
		if (CFG.INTERNET) Helper.drawContainer(Game.getWidth() / 2 - TextButton.WIDTH - 15, Game.getHeight() - TextButton.HEIGHT * 3 / 2, TextButton.WIDTH * 2 + 30, TextButton.HEIGHT * 2, false, false, g);
		
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
					if (goingto == 1) Game.currentFrame.addLayer(ll);
					if (goingto == 2) Game.currentFrame.addLayer(new JoinLayer());
					if (goingto == 3) Game.currentFrame.addLayer(new WeaponryLayer(true));
					
					goingto = 0;
				}
			}.start();
		}
		
		if (Game.weapons != null)
		{
			components.get(0).enabled = Game.weapons.length() > 0;
			components.get(1).enabled = Game.weapons.length() > 0;
		}
		
		enabled = Game.currentGame.getActiveLayer().equals(this);
	}
	
	@Override
	public void init()
	{
		ll = new LobbyLayer();
		Game.pullWeapons();
		
		MenuButton start = new MenuButton("startGame", 0);
		start.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				goingto = 1;
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
				goingto = 2;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(joingame);
		MenuButton wpn = new MenuButton("weaponry", 2);
		wpn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				goingto = 3;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(wpn);
		MenuButton opt = new MenuButton("options", 3);
		opt.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.addLayer(new SettingsLayer());
			}
		});
		components.add(opt);
		MenuButton end = new MenuButton("endGame", 4);
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
	public void onPacketReceived(Packet p, InetAddress ip, int port)
	{
		ll.onPacketReceived(p, ip, port);
	}
}
