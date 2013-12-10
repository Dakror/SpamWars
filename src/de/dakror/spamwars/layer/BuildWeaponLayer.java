package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.game.weapon.Part.Category;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.ui.WeaponryButton;
import de.dakror.spamwars.ui.WeaponryGroup;
import de.dakror.spamwars.ui.WeaponryPart;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class BuildWeaponLayer extends MPLayer
{
	WeaponryGroup categories;
	WeaponryGroup[] groups;
	
	WeaponryPart selectedPart;
	WeaponryButton selectedButton;
	
	public static Rectangle buildPlate;
	
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
		
		if (selectedPart != null) selectedPart.draw(g);
	}
	
	@Override
	public void update(int tick)
	{
		if (selectedPart != null)
		{
			int x = Game.currentGame.mouse.x - selectedPart.width / 2;
			int y = Game.currentGame.mouse.y - selectedPart.height / 2;
			
			int snap = 10;
			int fac = 5;
			
			for (Component c : components)
			{
				if (c instanceof WeaponryPart)
				{
					boolean xIs = Assistant.isBetween(x, c.x, c.x + c.width) || Assistant.isBetween(x + selectedPart.width, c.x, c.x + c.width) || Assistant.isBetween(c.x, x, x + selectedPart.width) || Assistant.isBetween(c.x + c.width, x, x + selectedPart.width);
					boolean yIs = Assistant.isBetween(y, c.y, c.y + c.height) || Assistant.isBetween(y + selectedPart.height, c.y, c.y + c.height) || Assistant.isBetween(c.y, y, y + selectedPart.height) || Assistant.isBetween(c.y + c.height, y, y + selectedPart.height);
					if (Math.abs(x - (c.x + c.width)) < snap && yIs) x = c.x + c.width - fac;
					if (Math.abs((x + selectedPart.width) - c.x) < snap && yIs) x = c.x - selectedPart.width + fac;
					
					if (Math.abs(y - (c.y + c.height)) < snap && xIs) y = c.y + c.height - fac;
					if (Math.abs((y + selectedPart.height) - c.y) < snap && xIs) y = c.y - selectedPart.height + fac;
				}
			}
			
			selectedPart.x = x;
			selectedPart.y = y;
			
		}
		// for (WeaponryPart c : parts)
		// {
		// // categories.getButton(c.part.getCategory().ordinal()).enabled = !c.enabled;
		// // if (c.enabled)
		// // {
		// // categories.onUnselect.trigger();
		// // categories.getButton(c.part.getCategory().ordinal()).selected = false;
		// // }
		//
		// if (!c.enabled) parts.remove(c);
		// }
		
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
			if (buildPlate.contains(selectedPart.x, selectedPart.y, selectedPart.width, selectedPart.height))
			{
				WeaponryPart p = new WeaponryPart(selectedPart.x, selectedPart.y, selectedPart.part);
				components.add(p);
				selectedPart = null;
				selectedButton.selected = false;
				selectedButton = null;
				removeGroups();
				categories.deselectAll();
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3 && selectedPart != null)
		{
			selectedPart = null;
			selectedButton.selected = false;
			selectedButton = null;
		}
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
				categories = new WeaponryGroup(0, 0);
				categories.onUnselect = new ClickEvent()
				{
					@Override
					public void trigger()
					{
						removeGroups();
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
					final WeaponryButton b = new WeaponryButton(part.getIcon());
					b.loseSelectionOnRMB = true;
					b.addClickEvent(new ClickEvent()
					{
						@Override
						public void trigger()
						{
							selectedPart = new WeaponryPart(Game.currentGame.mouse.x - part.getIcon().width / 2, Game.currentGame.mouse.y - part.getIcon().height / 2, part);
							selectedButton = b;
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
							removeGroups();
							components.add(groups[j]);
						}
					});
				}
			}
		}.start();
	}
	
	public void removeGroups()
	{
		for (int i = 2; i < components.size(); i++)
		{
			if (components.get(i) instanceof WeaponryGroup) components.remove(i);
		}
	}
}
