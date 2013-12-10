package de.dakror.spamwars.game.weapon;

import java.util.ArrayList;

/**
 * @author Dakror
 */
public class WeaponData
{
	public class DataPart
	{
		public Part part;
		public double x, y;
		
		public DataPart(Part part, double x, double y)
		{
			this.part = part;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString()
		{
			return part.ordinal() + ":" + x + ":" + y;
		}
	}
	
	ArrayList<DataPart> parts;
	
	public WeaponData()
	{
		parts = new ArrayList<>();
	}
	
	public void addPart(Part p, double xP, double yP)
	{
		parts.add(new DataPart(p, xP, yP));
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
}
