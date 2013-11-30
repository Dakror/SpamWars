package de.dakror.spamwars.ui;

import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class TextButton extends Button
{
	String text;
	
	public TextButton(int x, int y, int width, int height, String text)
	{
		super(x, y, width, height);
		this.text = text;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawContainer(x, y, width, height, state == 1, state != 0, g);
		Helper.drawHorizontallyCenteredString(text, x, width, y + height / 2 + 14, g, 35);
	}
	
	@Override
	public void update(int tick)
	{}
}
