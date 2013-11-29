package de.dakror.spamwars;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.UpdateThread;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.util.Assistant;


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
		Game.ip = Assistant.getHamachiIP();
		if (Game.ip == null)
		{
			JOptionPane.showMessageDialog(null, "Um Spam Wars spielen zu können, musst du \"LogMeIn Hamachi\" installiert und\naktiv haben. Es wird zum Verbinden zu anderen Computern benötigt.", "Hamachi nicht vorhanden", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		Game.currentFrame.setFullscreen();
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
