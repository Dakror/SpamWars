package de.dakror.spamwars.ui.weaponry;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.WeaponData;

/**
 * @author Dakror
 */
public class WeaponryWeaponButton extends ClickableComponent
{
	public static final int WIDTH = 450;
	public static final int HEIGHT = 300;
	
	WeaponData data;
	BufferedImage image;
	
	public boolean selected;
	
	public WeaponryWeaponButton(int x, int y, WeaponData data)
	{
		super(x, y, WIDTH, HEIGHT);
		this.data = data;
		image = data.getImage();
		Dimension dim = Helper.scaleTo(new Dimension(image.getWidth(), image.getHeight()), new Dimension(width - 30, height - 30));
		if (dim.width < image.getWidth() || dim.height < image.getHeight()) image = Helper.toBufferedImage(image.getScaledInstance(dim.width, dim.height, BufferedImage.SCALE_SMOOTH));
		selected = false;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (!selected)
		{
			Helper.drawShadow(x, y, width, height, g);
			Helper.drawOutline(x, y, width, height, state != 0, g);
		}
		else
		{
			Helper.drawContainer(x, y, width, height, true, false, g);
		}
		g.drawImage(image, x + (width - image.getWidth()) / 2, y + (height - image.getHeight()) / 2, Game.w);
	}
	
	@Override
	public void update(int tick)
	{}
}
