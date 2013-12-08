package de.dakror.spamwars.game.weapon;

import java.awt.Rectangle;

/**
 * @author Dakror
 */
public enum Part
{
	
	PISTOL_SILVER_HANDLE(593, 1250, 64, 88, 0, Category.HANDLE),
	PISTOL_SILVER_BARREL(621, 1187, 205, 51, 0, Category.BARREL),
	PISTOL_SILVER_TRIGGER(671, 1254, 51, 31, 0, Category.TRIGGER),
	
	// -- coming much more soon(-ish) -- //
	;
	
	Rectangle tex;
	Category category;
	int price;
	
	private Part(int x, int y, int width, int height, int price, Category category)
	{
		this.category = category;
		this.price = price;
		tex = new Rectangle(x, y, width, height);
	}
	
	public Rectangle getTexture()
	{
		return tex;
	}
	
	public Category getCategory()
	{
		return category;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public enum Category
	{
		HANDLE,
		TRIGGER,
		BARREL,
		SCOPE,
		SUBBARREL,
		BARRELGRIP,
		HOOK,
		ACCESSORY,
		PULL,
		DRUM,
	}
}
