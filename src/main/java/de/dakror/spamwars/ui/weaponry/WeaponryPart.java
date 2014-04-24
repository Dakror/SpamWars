package de.dakror.spamwars.ui.weaponry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.layer.BuildWeaponLayer;

/**
 * @author Dakror
 */
public class WeaponryPart extends ClickableComponent
{
	public Part part;
	BufferedImage icon;
	
	public static Point dragStart;
	public static WeaponryPart dragInstance;
	
	public WeaponryPart(int x, int y, Part part)
	{
		super(x, y, part.tex.width, part.tex.height);
		
		this.part = part;
		icon = Game.getImage("weapon/explode.png").getSubimage(part.tex.x, part.tex.y, part.tex.width, part.tex.height);
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
		
		dragStart = null;
		dragInstance = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!contains(e.getX(), e.getY())) return;
		
		if (dragStart == null)
		{
			dragStart = new Point(e.getX() - x, e.getY() - y);
			dragInstance = this;
		}
		
		if (!dragInstance.equals(this)) return;
		
		if (BuildWeaponLayer.buildPlate.contains(e.getX() - dragStart.x, y, width, height)) x = e.getX() - dragStart.x;
		if (BuildWeaponLayer.buildPlate.contains(x, e.getY() - dragStart.y, width, height)) y = e.getY() - dragStart.y;
	}
}
