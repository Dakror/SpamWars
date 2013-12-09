package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.game.weapon.Part.Category;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.WeaponryButton;
import de.dakror.spamwars.ui.WeaponryGroup;

/**
 * @author Dakror
 */
public class WeaponryLayer extends MPLayer
{
	WeaponryGroup[] groups;
	
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
		new Thread()
		{
			@Override
			public void run()
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
				
				WeaponryGroup categories = new WeaponryGroup(0, 0);
				categories.extending = true;
				components.add(categories);
				
				groups = new WeaponryGroup[Category.values().length];
				
				for (Category c : Category.values())
				{
					categories.addButton(new WeaponryButton(c.getIcon()));
					
					groups[c.ordinal()] = new WeaponryGroup(WeaponryButton.SIZE + 40, 0);
				}
				
				for (Part part : Part.values())
					groups[part.getCategory().ordinal()].addButton(new WeaponryButton(part.getIcon()));
				
				for (int i = 0; i < categories.length(); i++)
				{
					final int j = i;
					
					if (groups[i].length() == 0) categories.getButton(i).enabled = false;
					else categories.getButton(i).addClickEvent(new ClickEvent()
					{
						@Override
						public void trigger()
						{
							if (components.size() < 3)
							{
								components.add(groups[j]);
							}
							else
							{
								components.get(2).enabled = false;
								components.get(2).update(0);
								components.set(2, groups[j]);
								components.get(2).enabled = true;
							}
						}
					});
				}
			}
		}.start();
	}
}
