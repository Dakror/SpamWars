package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.game.weapon.Part.Category;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.WeaponPart;
import de.dakror.spamwars.ui.WeaponryButton;
import de.dakror.spamwars.ui.WeaponryGroup;

/**
 * @author Dakror
 */
public class BuildWeaponLayer extends MPLayer
{
	WeaponryGroup[] groups;
	
	CopyOnWriteArrayList<WeaponPart> parts = new CopyOnWriteArrayList<WeaponPart>();
	
	Part selectedPart;
	
	Rectangle buildPlate;
	
	public BuildWeaponLayer()
	{
		modal = true;
		
		int m = 200;
		buildPlate = new Rectangle(m * 3 / 2, m, Game.getWidth() - m * 2, Game.getHeight() - m * 2);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/weaponry.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		
		Helper.drawShadow(buildPlate.x, buildPlate.y, buildPlate.width, buildPlate.height, g);
		Helper.drawOutline(buildPlate.x, buildPlate.y, buildPlate.width, buildPlate.height, false, g);
		
		Helper.drawContainer(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() - TextButton.HEIGHT * 3 / 2, TextButton.WIDTH * 2, TextButton.HEIGHT * 2, false, false, g);
		
		drawComponents(g);
		
		if (selectedPart != null)
		{
			Rectangle ic = selectedPart.getIcon();
			Helper.drawImage(Game.getImage("weapon/explode.png"), Game.currentGame.mouse.x - ic.width / 2, Game.currentGame.mouse.y - ic.height / 2, ic.width, ic.height, ic.x, ic.y, ic.width, ic.height, g);
		}
	}
	
	@Override
	public void update(int tick)
	{
		for (Component c : components)
		{
			if (c instanceof WeaponPart)
			{
				if (!c.enabled)
				{
					components.remove(c);
					parts.remove(c);
				}
			}
		}
		
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1 && enabled)
		{
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread()
			{
				@Override
				public void run()
				{
					Game.currentGame.removeLayer(BuildWeaponLayer.this);
				}
			}.start();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		if (e.getButton() == MouseEvent.BUTTON1 && selectedPart != null)
		{
			int x = e.getX() - selectedPart.getIcon().width / 2;
			int y = e.getY() - selectedPart.getIcon().height / 2;
			
			if (buildPlate.contains(x, y, selectedPart.getIcon().width, selectedPart.getIcon().height))
			{
				WeaponPart p = new WeaponPart(x, y, selectedPart);
				components.add(p);
				parts.add(p);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3) selectedPart = null;
		
	}
	
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
		
		new Thread()
		{
			@Override
			public void run()
			{
				WeaponryGroup categories = new WeaponryGroup(0, 0);
				categories.onUnselect = new ClickEvent()
				{
					@Override
					public void trigger()
					{
						if (components.size() > 2) components.remove(2);
					}
				};
				categories.extending = true;
				components.add(categories);
				
				groups = new WeaponryGroup[Category.values().length];
				
				for (Category c : Category.values())
				{
					categories.addButton(new WeaponryButton(c.getIcon()));
					
					groups[c.ordinal()] = new WeaponryGroup(WeaponryButton.SIZE + 40, 0);
				}
				
				for (final Part part : Part.values())
				{
					WeaponryButton b = new WeaponryButton(part.getIcon());
					b.loseSelectionOnRMB = true;
					b.addClickEvent(new ClickEvent()
					{
						@Override
						public void trigger()
						{
							selectedPart = part;
						}
					});
					groups[part.getCategory().ordinal()].addButton(b);
				}
				
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
