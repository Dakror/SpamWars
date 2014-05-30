package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends Layer
{
	int goTo;
	
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
				goTo = 1;
			}
		});
		components.add(start);
		MenuButton joingame = new MenuButton("joinGame", 1);
		joingame.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
				goTo = 2;
			}
		});
		components.add(joingame);
		MenuButton wpn = new MenuButton("weaponry", 2);
		wpn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
				goTo = 3;
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
				Game.warp.disconnect();
			}
		});
		components.add(end);
	}
	
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
		
		if (Game.currentGame.alpha == 1 && goTo > 0)
		{
			if (goTo == 3) Game.currentGame.addLayer(new WeaponryLayer(true));
			Game.currentGame.fadeTo(0, 0.05f);
			
			goTo = 0;
		}
	}
}
