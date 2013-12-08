package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class WeaponryLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/weaponry.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
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
					Game.currentGame.removeLayer(WeaponryLayer.this);
				}
			}.start();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{
		TextButton back = new TextButton((Game.getWidth() - TextButton.WIDTH) / 2, Game.getHeight() - TextButton.HEIGHT, "Zurück");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(back);
	}
}
