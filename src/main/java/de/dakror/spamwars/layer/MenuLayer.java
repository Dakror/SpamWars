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

import java.awt.Font;
import java.awt.Graphics2D;
import java.net.InetAddress;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer {
	int goingto;
	
	static LobbyLayer ll;
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		if (!CFG.INTERNET && Game.user.getIP() != null) {
			Font old = g.getFont();
			g.setFont(new Font("", Font.PLAIN, 20));
			Helper.drawHorizontallyCenteredString("Offline-Modus: " + Game.user.getUsername() + " (" + Game.user.getIP().getHostAddress() + ")", Game.getWidth(), 20, g, 20);
			g.setFont(old);
		}
		
		Helper.drawString(DakrorBin.buildDate, 10, Game.getHeight() - 10, g, 18);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick) {
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1 && enabled) {
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread() {
				@Override
				public void run() {
					if (goingto == 1) Game.currentFrame.addLayer(ll);
					if (goingto == 2) Game.currentFrame.addLayer(new JoinLayer());
					if (goingto == 3) Game.currentFrame.addLayer(new WeaponryLayer(true));
					
					goingto = 0;
				}
			}.start();
		}
		
		if (Game.weapons != null) {
			components.get(0).enabled = Game.weapons.length() > 0;
			components.get(1).enabled = Game.weapons.length() > 0;
		}
		
		enabled = Game.currentGame.getActiveLayer().equals(this);
	}
	
	@Override
	public void init() {
		ll = new LobbyLayer();
		Game.pullWeapons();
		Game.pullMoney();
		
		MenuButton start = new MenuButton("startGame", 0);
		start.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				goingto = 1;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(start);
		MenuButton joingame = new MenuButton("joinGame", 1);
		joingame.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				goingto = 2;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(joingame);
		MenuButton wpn = new MenuButton("weaponry", 2);
		wpn.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				goingto = 3;
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(wpn);
		MenuButton opt = new MenuButton("options", 3);
		opt.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				Game.currentGame.addLayer(new SettingsLayer());
			}
		});
		components.add(opt);
		MenuButton end = new MenuButton("endGame", 4);
		end.addClickEvent(new ClickEvent() {
			@Override
			public void trigger() {
				System.exit(0);
			}
		});
		components.add(end);
	}
	
	@Override
	public void onPacketReceived(Packet p, InetAddress ip, int port) {
		ll.onPacketReceived(p, ip, port);
	}
}
