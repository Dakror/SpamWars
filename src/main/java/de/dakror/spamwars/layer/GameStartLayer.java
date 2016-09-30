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


package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.net.InetAddress;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet03Attribute;

/**
 * @author Dakror
 */
public class GameStartLayer extends MPLayer {
	int cd = 5;
	
	public GameStartLayer(boolean pregame) {
		modal = pregame;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (modal) drawModality(g);
		
		Color c = g.getColor();
		if (!modal) g.setColor(Color.gray);
		Helper.drawHorizontallyCenteredString("Spiel startet in", Game.getWidth(), Game.getHeight() / 3, g, 70);
		
		int cd = this.cd;
		if (cd < 0) cd = 0;
		
		Helper.drawHorizontallyCenteredString(cd + "", Game.getWidth(), Game.getHeight() / 2, g, 150);
		g.setColor(c);
	}
	
	@Override
	public void update(int tick) {
		if (cd == 0 && Game.server != null && modal) {
			Game.currentGame.removeLayer(this);
			Game.server.startGame();
			cd = -1;
		}
		if (cd == 0 && !modal) {
			Game.currentGame.removeLayer(this);
			Game.player.getWeapon().enabled = true;
		}
	}
	
	@Override
	public void onPacketReceived(Packet p, InetAddress ip, int port) {
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("countdown")) cd = Integer.parseInt(((Packet03Attribute) p).getValue());
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("worldsize") && modal) Game.currentGame.fadeTo(1, 0.05f);
	}
	
	@Override
	public void init() {}
}
