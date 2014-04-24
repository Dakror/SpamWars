package de.dakror.spamwars;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.UpdateThread;
import de.dakror.spamwars.game.weapon.Part;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.settings.CFG;

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
		
		if (!CFG.INTERNET)
		{
			JOptionPane.showMessageDialog(null, "Um Spam Wars spielen zu können, benötigst du eine aktive Internetverbindung.", "Internetverbindung nicht vorhanden", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		CFG.loadSettings();
		Part.init();
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		try
		{
			DakrorBin.init(Game.w, "SpamWars");
			Game.currentFrame.setFullscreen();
			// Game.currentFrame.w.setSize(1280, 720);
		}
		catch (IllegalStateException e)
		{
			System.exit(0);
		}
		Game.currentGame.addLayer(new MenuLayer());
		if (Game.user == null) Game.currentGame.addLayer(new MenuLayer());
		
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
