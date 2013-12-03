package de.dakror.spamwars.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author Dakror
 */
public class Assistant
{
	public static boolean intersection(Polygon p, Line2D line)
	{
		for (int i = 0; i < p.npoints; i++)
		{
			for (int j = 0; j < p.npoints; j++)
			{
				if (i == j) continue;
				
				Line2D l = new Line2D.Float(p.xpoints[i], p.ypoints[i], p.xpoints[j], p.ypoints[j]);
				if (l.intersectsLine(line)) return true;
			}
		}
		
		return false;
	}
	
	public static InetAddress getHamachiIP()
	{
		try
		{
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : interfaces)
			{
				if (ni.getDisplayName().equals("Hamachi Network Interface")) return ni.getInetAddresses().nextElement();
			}
			
			System.err.println(interfaces);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
