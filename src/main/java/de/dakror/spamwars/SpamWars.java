package de.dakror.spamwars;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.UpdateThread;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class SpamWars
{
	public static void main(String[] args) throws SocketException
	{
		CFG.INTERNET = Helper.isInternetReachable();
		
		// Launch.init(args);
		Launch.username = "Dakror";
		Launch.pwdMd5 = "94ff26c949ee6cfe131e551d802565a9";
		CFG.init();
		CFG.loadSettings();
		Part.init();
		
		try
		{
			Game.ip = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		
		if (!CFG.INTERNET)
		{
			Game.user = new User("Player" + (int) (Math.random() * 10000), Game.ip, 0);
			Game.money = 999999;
		}
		else
		{
			Game.user = new User(Launch.username, Game.ip, 0);
		}
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		
		DakrorBin.init(Game.w, "SpamWars");
		try
		{
			Game.currentFrame.setFullscreen();
			// Game.currentFrame.setWindowed(1280, 720);
		}
		catch (IllegalStateException e)
		{
			System.exit(0);
		}
		Game.currentGame.addLayer(new MenuLayer());
		
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
