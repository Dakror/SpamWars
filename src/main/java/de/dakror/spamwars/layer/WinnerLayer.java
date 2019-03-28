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

import java.awt.Graphics2D;
import java.net.InetAddress;
import java.util.Arrays;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class WinnerLayer extends MPLayer {
    User winner;

    public WinnerLayer() {
        modal = true;
        User[] users = Game.client.playerList.getUsers();
        Arrays.sort(users, HUDLayer.getSorter());
        winner = users[0];

        Game.player.stop();
    }

    @Override
    public void draw(Graphics2D g) {
        drawModality(g);

        Helper.drawHorizontallyCenteredString("1st Place: ", Game.getWidth(), Game.getHeight() / 3, g, 60);
        Helper.drawHorizontallyCenteredString(winner.getUsername(), Game.getWidth(), Game.getHeight() / 3 + 120, g, 150);
        Helper.drawHorizontallyCenteredString("(" + winner.K + " / " + winner.D + ")", Game.getWidth(), Game.getHeight() / 3 + 180, g, 30);
        drawComponents(g);
    }

    @Override
    public void update(int tick) {
        updateComponents(tick);
    }

    @Override
    public void onPacketReceived(Packet p, InetAddress ip, int port) {}

    @Override
    public void init() {
        TextButton next = new TextButton((Game.getWidth() - TextButton.WIDTH) / 2, Game.getHeight() / 3 * 2, "Continue");
        next.addClickEvent(new ClickEvent() {
            @Override
            public void trigger() {
                Game.world = null;
                Game.player = null;

                if (Game.server != null) {
                    Game.server.lobby = true;
                    Game.server.world = null;
                    Game.server.resetScoreboard();
                }

                Game.currentGame.setLayer(new MenuLayer());
                Game.currentGame.addLayer(new LobbyLayer());
            }
        });
        components.add(next);
    }
}
