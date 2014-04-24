package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.io.IOException;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet01Disconnect.Cause;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet14Login;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer
{
	boolean gotoweapon;
	public static boolean waiting = true;
	static LobbyLayer ll;
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawString("Version " + DakrorBin.buildDate, 10, Game.getHeight() - 10, g, 18);
		if (waiting)
		{
			drawModality(g);
			Helper.drawHorizontallyCenteredString("Warte auf Server...", Game.getWidth(), Game.getHeight() / 2, g, 60);
		}
		else drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (waiting) return;
		
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
				try
				{
					Game.client.sendPacketToCentral(new Packet01Disconnect(Launch.username, Cause.USER_DISCONNECT, false));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		components.add(end);
		
		
		
		try
		{
			if (waiting)
			{
				for (Component c : components)
					c.enabled = false;
				Game.client.sendPacketToCentral(new Packet14Login(Launch.username, Launch.pwdMd5));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p.getType() == PacketTypes.LOGIN)
		{
			for (Component c : components)
				c.enabled = true;
			waiting = false;
		}
		
		if (p.getType() == PacketTypes.REJECT)
		{
			Game.currentGame.addLayer(new Alert(((Packet02Reject) p).getCause().getDescription(), new ClickEvent()
			{
				@Override
				public void trigger()
				{
					System.exit(0);
				}
			}));
		}
		
		ll.onPacketReceived(p);
	}
}
