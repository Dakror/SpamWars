package de.dakror.spamwars.game.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.ui.ClickEvent;
import de.dakror.spamwars.game.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends Layer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1)
		{
			Game.currentFrame.layers.remove(this);
			Game.currentFrame.addLayer(new LobbyLayer());
			Game.currentFrame.fadeTo(0, 0.05f);
		}
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
				// Game.currentGame.initWorld();
				//
				// Game.currentFrame.layers.remove(MenuLayer.this);
			}
		});
		components.add(start);
		MenuButton join = new MenuButton("joinGame", 1);
		components.add(join);
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
}
