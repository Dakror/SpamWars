package de.dakror.spamwars;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dakror.gamesetup.util.Helper;
import de.dakror.reporter.Reporter;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.UpdateThread;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.layer.LoginLayer;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.util.Assistant;
import de.dakror.universion.UniVersion;

/**
 * @author Dakror
 */
public class SpamWars
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		CFG.INTERNET = Helper.isInternetReachable();
		
		CFG.init();
		
		UniVersion.offline = !CFG.INTERNET;
		
		UniVersion.init(SpamWars.class, CFG.VERSION, CFG.PHASE);
		if (!UniVersion.offline) Reporter.init(new File(CFG.DIR, "log"));
		
		if (!CFG.INTERNET)
		{
			try
			{
				Game.ip = InetAddress.getLocalHost();
				CFG.p(Game.ip);
				Game.user = new User("Player" + (int) (Math.random() * 10000), Game.ip, 0);
				Game.pullMoney();
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Game.ip = Assistant.getHamachiIP();
			if (Game.ip == null)
			{
				JOptionPane.showMessageDialog(null, "Um Spam Wars spielen zu können, musst du \"LogMeIn Hamachi\" installiert und\naktiv haben. Es wird zum Verbinden zu anderen Computern benötigt.", "Hamachi nicht vorhanden", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
		
		CFG.loadSettings();
		Part.init();
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		try
		{
			Game.currentFrame.setFullscreen();
			// Game.currentFrame.w.setSize(1280, 720);
		}
		catch (IllegalStateException e)
		{
			System.exit(0);
		}
		Game.currentGame.addLayer(new MenuLayer());
		if (Game.user == null) Game.currentGame.addLayer(new LoginLayer());
		
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
