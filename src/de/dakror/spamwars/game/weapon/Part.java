package de.dakror.spamwars.game.weapon;

import java.awt.Rectangle;

/**
 * @author Dakror
 */
public enum Part
{
	// -- !!! Don't change the order, it is important for weapon saves !!! -- //
	
	PISTOL_SILVER_HANDLE(593, 1250, 64, 88, 0, Category.HANDLE),
	PISTOL_SILVER_BARREL(621, 1187, 205, 51, 0, Category.BARREL),
	PISTOL_SILVER_TRIGGER(671, 1254, 51, 31, 0, Category.TRIGGER),
	
	MINIGUN_BARREL(1378, 1006, 456, 83, 5000, Category.BARREL),
	
	ASSAULT_RIFLE_GRAY_SHOULDER(95, 2074, 237, 115, 500, Category.SHOULDER),
	
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
	
	public Rectangle getIcon()
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
		HANDLE(202, 1538, 87, 103),
		TRIGGER(609, 983, 51, 31),
		BARREL(1484, 1842, 525, 40),
		SHOULDER(95, 2074, 237, 115),
		SCOPE(363, 1855, 273, 83),
		SUBBARREL(1385, 1146, 328, 28),
		BARRELGRIP(1381, 1101, 230, 37),
		ACCESSORY(1253, 462, 149, 91),
		HAMMER(1244, 908, 77, 98),
		DRUM(245, 422, 76, 52),
		BACKGROUND(248, 283, 74, 64),
		
		;
		private Rectangle icon;
		
		private Category(int x, int y, int width, int height)
		{
			icon = new Rectangle(x, y, width, height);
		}
		
		public Rectangle getIcon()
		{
			return icon;
		}
	}
}
