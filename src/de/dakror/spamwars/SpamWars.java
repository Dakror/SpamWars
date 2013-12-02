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
		if (!CFG.INTERNET)
		{
			try
			{
				Game.ip = InetAddress.getLocalHost();
				CFG.p(Game.ip);
				Game.user = new User("Player" + (int) (Math.random() * 10000), Game.ip, 0);
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
		
		UniVersion.offline = !CFG.INTERNET;
		
		UniVersion.init(SpamWars.class, CFG.VERSION, CFG.PHASE);
		if (!UniVersion.offline) Reporter.init(new File(CFG.DIR, "log"));
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		Game.currentFrame.setFullscreen();
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
