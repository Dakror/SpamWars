package de.dakror.spamwars.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CFG
{
	public static final File DIR = new File(System.getProperty("user.home") + "/.dakror/SpamWars");
	
	// -- UniVersion -- //
	public static final int VERSION = 2013120811;
	public static final int PHASE = 1;
	
	public static boolean INTERNET;
	
	static long time = 0;
	
	public static final void init()
	{
		DIR.mkdirs();
		
		File maps = new File(DIR, "maps");
		maps.mkdir();
		String[] files = { "BigSnowSurfaceStoneBasementDefault.map", "SmallGrassSurfaceStoneBasementDefault.map" };
		
		for (String s : files)
			Helper.setFileContent(new File(maps, s), Helper.getURLContent(CFG.class.getResource("/map/" + s)));
	}
	
	// -- user settings -- //
	public static boolean AUTO_RELOAD = true;
	
	public static void saveSettings()
	{
		File s = new File(DIR, ".properties");
		try
		{
			s.createNewFile();
			Properties properties = new Properties();
			properties.setProperty("AUTO_RELOAD", Boolean.toString(AUTO_RELOAD));
			properties.store(new FileWriter(s), "");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadSettings()
	{
		File s = new File(DIR, ".properties");
		if (s.exists())
		{
			try
			{
				Properties properties = new Properties();
				properties.load(new FileReader(s));
				
				AUTO_RELOAD = properties.getProperty("AUTO_RELOAD").equals("true");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
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
