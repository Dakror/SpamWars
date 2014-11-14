package de.dakror.spamwars.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;

/**
 * @author Dakror
 */
public class CFG {
	public static final File DIR = new File(System.getProperty("user.home") + "/.dakror/SpamWars");
	
	public static boolean INTERNET;
	
	static long time = 0;
	
	static InetAddress broadCastAddress;
	static InetAddress address;
	
	public static final void init() {
		DIR.mkdirs();
		GameFrame.screenshotDir = new File(DIR, "screenshots");
		
		
		File maps = new File(DIR, "maps");
		maps.mkdir();
		String[] files = { "BigSnowSurfaceStoneBasementDefault.map", "SmallGrassSurfaceStoneBasementDefault.map" };
		
		for (String s : files)
			Helper.setFileContent(new File(maps, s), Helper.getURLContent(CFG.class.getResource("/map/" + s)));
		
		try {
			List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
			
			for (NetworkInterface ni : nis) {
				if (!ni.getInetAddresses().hasMoreElements()) continue;
				
				for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
					if (ia.getAddress().isLoopbackAddress() || ia.getBroadcast() == null) continue;
					
					broadCastAddress = ia.getBroadcast();
					address = ia.getAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static InetAddress getBroadcastAddress() {
		return broadCastAddress;
	}
	
	public static InetAddress getAddress() {
		return address;
	}
	
	// -- user settings -- //
	public static boolean AUTO_RELOAD = true;
	
	public static void saveSettings() {
		File s = new File(DIR, ".properties");
		try {
			s.createNewFile();
			Properties properties = new Properties();
			properties.setProperty("AUTO_RELOAD", Boolean.toString(AUTO_RELOAD));
			properties.store(new FileWriter(s), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadSettings() {
		File s = new File(DIR, ".properties");
		if (s.exists()) {
			try {
				Properties properties = new Properties();
				properties.load(new FileReader(s));
				
				AUTO_RELOAD = properties.getProperty("AUTO_RELOAD").equals("true");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Properties loadLogin() {
		try {
			File s = new File(DIR, ".login");
			
			if (!s.exists()) return null;
			
			Properties properties = new Properties();
			properties.load(new FileReader(s));
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void deleteLogin() {
		File s = new File(DIR, ".login");
		s.delete();
	}
	
	public static void saveLogin(String username, String pwd) {
		try {
			File s = new File(DIR, ".login");
			Properties properties = new Properties();
			properties.setProperty("username", username);
			properties.setProperty("pwd", pwd);
			properties.store(new FileWriter(s), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// -- debug profiling -- //
	public static void u() {
		if (time == 0) time = System.currentTimeMillis();
		else {
			p(System.currentTimeMillis() - time);
			time = 0;
		}
	}
	
	public static void p(Object... p) {
		if (p.length == 1) System.out.println(p[0]);
		else System.out.println(Arrays.toString(p));
	}
}
