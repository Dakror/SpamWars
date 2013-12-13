package de.dakror.spamwars.game.weapon;

import java.awt.Rectangle;

import de.dakror.spamwars.game.weapon.Weapon.FireMode;

/**
 * @author Dakror
 */
public enum Part
{
	// -- !!! Don't change the order, it is important for weapon saves !!! -- //
	
	PISTOL_SILVER_HANDLE(593, 1250, 64, 88, 0, 10, 9, 50, 35, FireMode.SINGLE, Category.HANDLE),
	PISTOL_SILVER_BARREL(621, 1187, 205, 51, 0, 20, 9, 50, 35, FireMode.SINGLE, Category.BARREL),
	PISTOL_SILVER_TRIGGER(671, 1254, 51, 31, 0, 10, 9, 50, 35, FireMode.SINGLE, Category.TRIGGER),
	
	MINIGUN_BARREL(1378, 1006, 456, 83, 5000, 3, 100, 1, 1000, FireMode.AUTO, Category.BARREL),
	
	ASSAULT_RIFLE_GRAY_SHOULDER(95, 2074, 237, 115, 500, 3, 30, 30, 150, FireMode.AUTO, Category.SHOULDER),
	
	// -- coming much more soon(-ish) -- //
	;
	
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
	
	public static int highest_speed = 0;
	public static int highest_magazine = 0;
	public static float highest_angle = 0;
	public static int highest_reload = 0;
	
	static
	{
		highest_speed = getHighestSpeed();
		highest_magazine = getHighestMagazine();
		highest_angle = getHighestAngle();
		highest_reload = getHighestReload();
	}
	
	Rectangle tex;
	Category category;
	int price, speed, magazine, reload;
	float angle;
	FireMode mode;
	
	private Part(int x, int y, int width, int height, int price, int speed, int magazine, float angle, int reload, FireMode mode, Category category)
	{
		this.category = category;
		this.price = price;
		this.speed = speed;
		this.magazine = magazine;
		this.angle = angle;
		this.reload = reload;
		this.mode = mode;
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
	
	public int getSpeed()
	{
		return speed;
	}
	
	public int getMagazine()
	{
		return magazine;
	}
	
	public int getReload()
	{
		return reload;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	public FireMode getMode()
	{
		return mode;
	}
	
	// -- highest-X functions -- //
	
	public static int getHighestSpeed()
	{
		int x = 0;
		for (Part p : values())
			if (p.speed > x) x = p.speed;
		return x;
	}
	
	public static int getHighestMagazine()
	{
		int x = 0;
		for (Part p : values())
			if (p.magazine > x) x = p.magazine;
		return x;
	}
	
	public static float getHighestAngle()
	{
		float x = 0;
		for (Part p : values())
			if (p.angle > x) x = p.angle;
		return x;
	}
	
	public static int getHighestReload()
	{
		int x = 0;
		for (Part p : values())
			if (p.reload > x) x = p.reload;
		return x;
	}
}
