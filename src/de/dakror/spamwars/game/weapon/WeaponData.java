package de.dakror.spamwars.game.weapon;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.Part.Category;

/**
 * @author Dakror
 */
public class WeaponData
{
	ArrayList<DataPart> parts;
	
	public WeaponData()
	{
		parts = new ArrayList<>();
	}
	
	public void addPart(Part p, double x, double y)
	{
		parts.add(new DataPart(p, x, y));
	}
	
	public void addPart(DataPart dp)
	{
		parts.add(dp);
	}
	
	public int getPrice()
	{
		int price = 0;
		
		for (DataPart p : parts)
			price += p.part.getPrice();
		
		return price;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for (DataPart p : parts)
		{
			s += p.toString() + ";";
		}
		s = s.substring(0, s.length() - 1);
		
		return s;
	}
	
	public DataPart getPart(Category category)
	{
		for (DataPart p : parts)
		{
			if (p.part.category == category) return p;
		}
		
		return null;
	}
	
	/**
	 * Call after any modification to data is done!
	 */
	public WeaponData getSortedData()
	{
		WeaponData wd = new WeaponData();
		double minX = -1337, minY = -1337;
		
		for (DataPart p : parts)
		{
			if (minX == -1337 || p.x < minX) minX = p.x;
			if (minY == -1337 || p.y < minY) minY = p.y;
		}
		
		for (DataPart p : parts)
		{
			p.x -= minX;
			p.y -= minY;
			wd.addPart(p.part, p.x, p.y);
		}
		
		return wd;
	}
	
	public DataPart getOrigin()
	{
		for (DataPart p : parts)
		{
			if (p.x == 0 && p.y == 0) return p;
		}
		
		return null;
	}
	
	public Point getGrab()
	{
		for (DataPart p : parts)
		{
			if (p.part.category == Category.HANDLE) return new Point((int) p.x + p.part.tex.width / 2, (int) p.y + p.part.tex.height / 2);
		}
		
		return null;
	}
	
	public Point getExit()
	{
		for (DataPart p : parts)
		{
			if (p.part.category == Category.BARREL) return new Point((int) p.x + p.part.tex.width, (int) p.y + p.part.tex.height / 2);
		}
		
		return null;
	}
	
	public BufferedImage getImage()
	{
		int width = 0, height = 0;
		for (DataPart p : parts)
		{
			if (p.x + p.part.tex.width > width) width = (int) (p.x + p.part.tex.width);
			if (p.y + p.part.tex.height > height) height = (int) (p.y + p.part.tex.height);
		}
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		for (DataPart p : parts)
		{
			Helper.drawImage2(Game.getImage("weapon/explode.png"), (int) p.x, (int) p.y, p.part.tex.width, p.part.tex.height, p.part.tex.x, p.part.tex.y, p.part.tex.width, p.part.tex.height, g);
		}
		
		return bi;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof WeaponData)
		{
			return toString().equals(obj.toString());
		}
		
		return false;
	}
	
	public static WeaponData load(String s)
	{
		WeaponData wd = new WeaponData();
		
		String[] parts = s.split(";");
		
		for (String p : parts)
		{
			wd.addPart(DataPart.load(p));
		}
		
		return wd;
	}
}
