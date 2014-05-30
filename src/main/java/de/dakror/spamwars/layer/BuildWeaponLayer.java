package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.DataPart;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.game.weapon.Part.Category;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.ui.weaponry.WeaponryButton;
import de.dakror.spamwars.ui.weaponry.WeaponryGroup;
import de.dakror.spamwars.ui.weaponry.WeaponryPart;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class BuildWeaponLayer extends Layer
{
	WeaponryGroup categories;
	WeaponryGroup[] groups;
	
	WeaponryPart selectedPart;
	WeaponryButton selectedButton;
	
	BufferedImage stats;
	
	TextButton auto;
	
	public static Rectangle buildPlate;
	
	WeaponData cacheData, exisData;
	
	int id;
	
	public BuildWeaponLayer(WeaponData data, int id)
	{
		modal = true;
		
		exisData = data;
		this.id = id;
		
		int m = 200;
		buildPlate = new Rectangle(m * 3 / 2, m, Game.getWidth() - m * 5 / 2, Game.getHeight() - m * 2);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/weaponry.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawShadow(buildPlate.x, buildPlate.y, buildPlate.width, buildPlate.height, g);
		Helper.drawOutline(buildPlate.x, buildPlate.y, buildPlate.width, buildPlate.height, false, g);
		
		Helper.drawContainer(Game.getWidth() / 2 - TextButton.WIDTH - 15, Game.getHeight() - TextButton.HEIGHT * 2 - 30, TextButton.WIDTH * 2 + 30, TextButton.HEIGHT * 3, false, false, g);
		
		g.drawImage(stats, buildPlate.x + buildPlate.width, buildPlate.y, Game.w);
		
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
		
		boolean hasParts = false, hasTrigger = false, hasHandle = false, hasBarrel = false;
		
		for (Component c : components)
		{
			if (c instanceof WeaponryPart)
			{
				if (!c.enabled)
				{
					categories.getButton(((WeaponryPart) c).part.category.ordinal()).enabled = true;
					components.remove(c);
					
					cacheData = getWeaponData();
				}
				else
				{
					categories.getButton(((WeaponryPart) c).part.category.ordinal()).enabled = false;
					hasParts = true;
					if (((WeaponryPart) c).part.category == Category.BARREL) hasBarrel = true;
					if (((WeaponryPart) c).part.category == Category.HANDLE) hasHandle = true;
					if (((WeaponryPart) c).part.category == Category.TRIGGER) hasTrigger = true;
				}
			}
		}
		
		components.get(1).enabled = hasParts && hasBarrel && hasHandle && hasTrigger;
		
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
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		
		cacheData = getWeaponData();
	}
	
	public void renderWeaponStats(WeaponData cacheData)
	{
		int width = 190, height = 170;
		stats = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) stats.getGraphics();
		
		Helper.drawShadow(0, 0, width, height, g);
		Helper.drawOutline(0, 0, width, height, false, g);
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(Game.w.getFont());
		g.setColor(Color.black);
		
		Helper.drawProgressBar(0 + 15, 0 + 15, width - 30, cacheData.getSpeed() / (float) Part.highest_speed, "7a36a3", g);
		Helper.drawHorizontallyCenteredString("Verzögerung", 0, width, 0 + 31, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 35, width - 30, cacheData.getMagazine() / (float) Part.highest_magazine, "ffc744", g);
		Helper.drawHorizontallyCenteredString("Munition", 0, width, 0 + 51, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 55, width - 30, cacheData.getAngle() / (float) Part.highest_angle, "009ab8", g);
		Helper.drawHorizontallyCenteredString("Winkel", 0, width, 0 + 71, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 75, width - 30, cacheData.getReload() / (float) Part.highest_reload, "a55212", g);
		Helper.drawHorizontallyCenteredString("Nachladen", 0, width, 0 + 91, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 95, width - 30, cacheData.getProjectileSpeed() / (float) Part.highest_projectileSpeed, "2a86e7", g);
		Helper.drawHorizontallyCenteredString("Schnelligkeit", 0, width, 0 + 111, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 115, width - 30, cacheData.getRange() / (float) Part.highest_range, "7dd33c", g);
		Helper.drawHorizontallyCenteredString("Reichweite", 0, width, 0 + 131, g, 15);
		
		Helper.drawProgressBar(0 + 15, 0 + 135, width - 30, cacheData.getDamage() / (float) Part.highest_damage, "ff3232", g);
		Helper.drawHorizontallyCenteredString("Schaden", 0, width, 0 + 151, g, 15);
	}
	
	@Override
	public void init()
	{
		cacheData = getWeaponData();
		
		TextButton back = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() - TextButton.HEIGHT - 10, "Zurück");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(back);
		
		TextButton build = new TextButton(Game.getWidth() / 2, Game.getHeight() - TextButton.HEIGHT - 10, "Bauen");
		build.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.addLayer(new PurchaseWeaponLayer(exisData, id, getWeaponData()));
			}
		});
		build.enabled = false;
		components.add(build);
		
		auto = new TextButton((Game.getWidth() - TextButton.WIDTH) / 2, Game.getHeight() - TextButton.HEIGHT * 2 - 10, "Manuell");
		auto.setToggleMode(true);
		if (exisData != null)
		{
			auto.setSelected(exisData.isAutomatic());
			if (exisData.isAutomatic()) auto.setText("Automatik");
		}
		
		auto.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				auto.setText(!auto.isSelected() ? "Manuell" : "Automatik");
			}
		});
		components.add(auto);
		
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
				
				@SuppressWarnings("unchecked")
				ArrayList<Part> parts = (ArrayList<Part>) Part.parts.clone();
				Collections.sort(parts, new Comparator<Part>()
				{
					@Override
					public int compare(Part o1, Part o2)
					{
						return o1.price - o2.price;
					}
				});
				
				for (final Part part : parts)
				{
					final WeaponryButton b = new WeaponryButton(part.tex);
					b.setPart(part);
					b.loseSelectionOnRMB = true;
					b.addClickEvent(new ClickEvent()
					{
						@Override
						public void trigger()
						{
							selectedPart = new WeaponryPart(Game.currentGame.mouse.x - part.tex.width / 2, Game.currentGame.mouse.y - part.tex.height / 2, part);
							selectedButton = b;
						}
					});
					groups[part.category.ordinal()].addButton(b);
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
				
				loadExistingWeapon();
			}
		}.start();
	}
	
	public void loadExistingWeapon()
	{
		if (exisData == null) return;
		
		double width = 0, height = 0;
		
		for (DataPart dp : exisData.getParts())
		{
			if (dp.x + dp.part.tex.width > width) width = dp.x + dp.part.tex.width;
			if (dp.y + dp.part.tex.height > height) height = dp.y + dp.part.tex.height;
		}
		
		for (DataPart dp : exisData.getParts())
		{
			WeaponryPart p = new WeaponryPart((int) (dp.x + (buildPlate.width - width) / 2) + buildPlate.x, (int) (dp.y + (buildPlate.height - height) / 2) + buildPlate.y, dp.part);
			components.add(p);
		}
		
		categories.deselectAll();
	}
	
	public void removeGroups()
	{
		for (int i = 4; i < components.size(); i++)
		{
			if (components.get(i) instanceof WeaponryGroup) components.remove(i);
		}
	}
	
	public WeaponData getWeaponData()
	{
		WeaponData data = new WeaponData();
		
		for (Component c : components)
		{
			if (c instanceof WeaponryPart)
			{
				WeaponryPart p = (WeaponryPart) c;
				data.addPart(p.part, p.x, p.y);
			}
		}
		
		if (auto != null) data.setAutomatic(auto.isSelected());
		
		data.calculateStats();
		
		renderWeaponStats(data);
		
		return data;
	}
}
