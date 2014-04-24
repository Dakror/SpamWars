package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer
{
	boolean gotoweapon;
	
	static LobbyLayer ll;
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawString("Version " + DakrorBin.buildDate, 10, Game.getHeight() - 10, g, 18);
		
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
					if (gotoweapon)
					{
						Game.currentFrame.addLayer(new WeaponryLayer(true));
						
						gotoweapon = false;
					}
					else Game.currentFrame.addLayer(ll);
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
		Game.pullMoney();
		Game.pullWeapons();
		ll = new LobbyLayer();
		
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
		MenuButton wpn = new MenuButton("weaponry", 2);
		wpn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				gotoweapon = true;
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
	public void onPacketReceived(Packet p)
	{
		ll.onPacketReceived(p);
	}
}