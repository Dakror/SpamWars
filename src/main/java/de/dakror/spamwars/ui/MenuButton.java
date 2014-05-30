package de.dakror.spamwars.ui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;

import de.dakror.gamesetup.ui.ClickableComponent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;

/**
 * @author Dakror
 */
public class MenuButton extends ClickableComponent
{
	String image;
	int y1;
	Dimension size;
	float alpha;
	float speed = 0.045f;
	float min = 0.6f;
	
	public MenuButton(String image, int y)
	{
		super(0, 0, 0, 100);
		this.image = image;
		y1 = y;
		size = new Dimension(0, 0);
		alpha = min;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Image img = Game.getImage("gui/" + image + ".png");
		int height = 100;
		int width = (height * img.getWidth(null)) / img.getHeight(null);
		Dimension s = Helper.getRelativeScaled(new Dimension(width, height), new Dimension(1920, 1080), new Dimension(Game.getWidth(), Game.getHeight()));
		this.width = s.width;
		this.height = s.height;
		x = (Game.getWidth() - this.width) / 2;
		y = Game.getHeight() / 3 + y1 * (s.height + 20);
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		Helper.drawImageCenteredRelativeScaled(img, y, width, height, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		g.setComposite(c);
	}
	
	@Override
	public void update(int tick)
	{
		if (!enabled) return;
		if (state == 2 || state == 1)
		{
			alpha += speed;
		}
		else if (alpha > min + speed)
		{
			alpha -= speed;
		}
		
		if (alpha > 1) alpha = 1;
		if (alpha < min) alpha = min;
	}
}
