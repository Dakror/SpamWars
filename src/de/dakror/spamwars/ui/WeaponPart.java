package de.dakror.spamwars.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part;

/**
 * @author Dakror
 */
public class WeaponPart extends ClickableComponent
{
	Part part;
	BufferedImage icon;
	
	public WeaponPart(int x, int y, Part part)
	{
		super(x, y, part.getIcon().width, part.getIcon().height);
		
		this.part = part;
		icon = Game.getImage("weapon/explode.png").getSubimage(part.getIcon().x, part.getIcon().y, part.getIcon().width, part.getIcon().height);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (!enabled) return;
		
		g.drawImage(icon, x, y, Game.w);
		
		if (state != 0)
		{
			Color o = g.getColor();
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			g.setColor(o);
		}
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		
		if (contains(e.getX(), e.getY()))
		{
			if (e.getButton() == MouseEvent.BUTTON3) enabled = false;
		}
	}
}
