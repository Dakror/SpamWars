package de.dakror.spamwars;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.UpdateThread;

/**
 * @author Dakror
 */
public class SpamWars
{
	public static void main(String[] args)
	{
		new Game();
		Game.currentFrame.init("Spam Wars");
		Game.currentFrame.setFullscreen();
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
