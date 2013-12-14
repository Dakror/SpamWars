package de.dakror.spamwars.game.weapon;

import java.awt.Rectangle;
import java.util.ArrayList;

import de.dakror.gamesetup.util.CSVReader;

/**
 * @author Dakror
 */
public class Part
{
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
	
	public enum FireMode
	{
		SINGLE,
		AUTO
	}
	
	public static ArrayList<Part> parts = new ArrayList<>();
	
	public static void init()
	{
		CSVReader csv = new CSVReader("/weapons.csv");
		csv.readRow(); // skip headings
		
		String cell = "";
		Part part = null;
		while ((cell = csv.readNext()) != null)
		{
			switch (csv.getIndex())
			{
				case 0:
					if (part != null) parts.add(part);
					part = new Part();
					part.id = parts.size();
					break;
				case 1:
					part.tex.x = Integer.parseInt(cell);
					break;
				case 2:
					part.tex.y = Integer.parseInt(cell);
					break;
				case 3:
					part.tex.width = Integer.parseInt(cell);
					break;
				case 4:
					part.tex.height = Integer.parseInt(cell);
					break;
				case 5:
					part.price = Integer.parseInt(cell);
					break;
				case 6:
					part.speed = Integer.parseInt(cell);
					break;
				case 7:
					part.magazine = Integer.parseInt(cell);
					break;
				case 8:
					part.angle = Integer.parseInt(cell);
					break;
				case 9:
					part.reload = Integer.parseInt(cell);
					break;
				case 10:
					part.projectileSpeed = Integer.parseInt(cell);
					break;
				case 11:
					part.range = Integer.parseInt(cell);
					break;
				case 12:
					part.damage = Integer.parseInt(cell);
					break;
				case 13:
					part.mode = FireMode.valueOf(cell);
					break;
				case 14:
					part.category = Category.valueOf(cell);
					break;
			}
		}
		
		if (part != null) parts.add(part);
		
		highest_speed = getHighestSpeed();
		highest_magazine = getHighestMagazine();
		highest_angle = getHighestAngle();
		highest_reload = getHighestReload();
		highest_projectileSpeed = getHighestProjectileSpeed();
		highest_range = getHighestRange();
		highest_damage = getHighestDamage();
	}
	
	public static int highest_speed = 0;
	public static int highest_magazine = 0;
	public static int highest_angle = 0;
	public static int highest_reload = 0;
	public static int highest_projectileSpeed = 0;
	public static int highest_range = 0;
	public static int highest_damage = 0;
	
	public Rectangle tex;
	public Category category;
	public int id, price, speed, magazine, angle, reload, projectileSpeed, range, damage;
	public FireMode mode;
	
	public Part()
	{
		tex = new Rectangle();
		category = null;
		mode = null;
		price = speed = magazine = reload = projectileSpeed = range = damage = 0;
		angle = 0;
	}
	
	@Override
	public String toString()
	{
		return "PART#" + id;
	}
	
	public static int getHighestSpeed()
	{
		int x = 0;
		for (Part p : parts)
			if (p.speed > x) x = p.speed;
		return x;
	}
	
	public static int getHighestMagazine()
	{
		int x = 0;
		for (Part p : parts)
			if (p.magazine > x) x = p.magazine;
		return x;
	}
	
	public static int getHighestAngle()
	{
		int x = 0;
		for (Part p : parts)
			if (p.angle > x) x = p.angle;
		return x;
	}
	
	public static int getHighestReload()
	{
		int x = 0;
		for (Part p : parts)
			if (p.reload > x) x = p.reload;
		return x;
	}
	
	public static int getHighestProjectileSpeed()
	{
		int x = 0;
		for (Part p : parts)
			if (p.projectileSpeed > x) x = p.projectileSpeed;
		return x;
	}
	
	public static int getHighestRange()
	{
		int x = 0;
		for (Part p : parts)
			if (p.range > x) x = p.range;
		return x;
	}
	
	public static int getHighestDamage()
	{
		int x = 0;
		for (Part p : parts)
			if (p.damage > x) x = p.damage;
		return x;
	}
}
