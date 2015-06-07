/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.spamwars;

import java.net.SocketException;

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
public class SpamWars {
	public static void main(String[] args) throws SocketException {
		CFG.INTERNET = Helper.isInternetReachable();
		
		Launch.init(args);
		CFG.init();
		CFG.loadSettings();
		Part.init();
		
		Game.ip = CFG.getAddress();
		
		if (!CFG.INTERNET) {
			Game.user = new User("Player" + (int) (Math.random() * 10000), Game.ip, 0);
			Game.money = 999999;
		} else {
			Game.user = new User(Launch.username, Game.ip, 0);
		}
		
		new Game();
		Game.currentFrame.init("Spam Wars");
		
		DakrorBin.init(Game.w, "SpamWars");
		try {
			Game.currentFrame.setFullscreen();
			// Game.currentFrame.setWindowed(1280, 720);
		} catch (IllegalStateException e) {
			System.exit(0);
		}
		Game.currentGame.addLayer(new MenuLayer());
		
		Game.currentFrame.updater = new UpdateThread();
		
		while (true)
			Game.currentFrame.main();
	}
}
