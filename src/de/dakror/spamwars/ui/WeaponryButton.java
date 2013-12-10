package de.dakror.spamwars.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class WeaponryButton extends ClickableComponent
{
	public static final int SIZE = 128;
	
	BufferedImage icon;
	
	boolean selected;
	public boolean loseSelectionOnRMB;
	
	public WeaponryButton(Rectangle icon)
	{
		super(0, 0, SIZE, SIZE);
		this.icon = Game.getImage("weapon/explode.png").getSubimage(icon.x, icon.y, icon.width, icon.height);
		
		Dimension dim = Helper.scaleTo(new Dimension(icon.width, icon.height), new Dimension(SIZE - 30, SIZE - 30));
		selected = false;
		this.icon = Helper.toBufferedImage(this.icon.getScaledInstance(dim.width, dim.height, BufferedImage.SCALE_SMOOTH));
		
		loseSelectionOnRMB = false;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (state == 0 && !selected)
		{
			if (enabled) Helper.drawShadow(x, y, width, height, g);
			Helper.drawOutline(x, y, width, height, false, g);
		}
		else Helper.drawContainer(x, y, width, height, false, state == 1 || selected, g);
		
		g.drawImage(icon, x + (width - icon.getWidth()) / 2, y + (height - icon.getHeight()) / 2, Game.w);
		
		int m = 9;
		
		if (!enabled) Helper.drawShadow(x - m, y - m, width + m * 2, height + m * 2, g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (contains(e.getX(), e.getY()) && enabled)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				if (selected)
				{
					selected = false;
					state = 2;
					return;
				}
				
				triggerEvents();
				selected = true;
			}
			else if (e.getButton() == MouseEvent.BUTTON3 && loseSelectionOnRMB) selected = false;
		}
	}
}
