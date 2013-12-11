package de.dakror.spamwars.game.weapon;

/**
 * @author Dakror
 */
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
	
	public static DataPart load(String s)
	{
		String[] parts = s.split(":");
		return new DataPart(Part.values()[Integer.parseInt(parts[0])], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
	}
}
