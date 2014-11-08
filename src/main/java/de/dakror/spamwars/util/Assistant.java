package de.dakror.spamwars.util;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
	
	public static boolean isBetween(int x, int min, int max)
	{
		return x >= min && x <= max;
	}
	
	
	static InetAddress broadCastAddress;
	
	public static InetAddress getBroadcastAddress() throws SocketException
	{
		if (broadCastAddress == null)
		{
			List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nis)
			{
				if (!ni.getInetAddresses().hasMoreElements()) continue;
				
				for (InterfaceAddress ia : ni.getInterfaceAddresses())
				{
					if (ia.getAddress().isLoopbackAddress() || ia.getBroadcast() == null) continue;
					
					broadCastAddress = ia.getBroadcast();
				}
			}
		}
		
		return broadCastAddress;
	}
}
