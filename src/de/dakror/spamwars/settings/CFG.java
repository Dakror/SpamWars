package de.dakror.spamwars.settings;

import java.util.Arrays;

/**
 * @author Dakror
 */
public class CFG
{
	public static final int PACKETSIZE = 255; // bytes
	public static final int SERVER_PORT = 19950; // bytes
	public static final int CLIENT_PORT = 19951; // bytes
	
	public static boolean INTERNET;
	
	static long time = 0;
	
	// -- debug profiling -- //
	public static void u()
	{
		if (time == 0) time = System.currentTimeMillis();
		else
		{
			p(System.currentTimeMillis() - time);
			time = 0;
		}
	}
	
	public static void p(Object... p)
	{
		if (p.length == 1) System.out.println(p[0]);
		else System.out.println(Arrays.toString(p));
	}
}
