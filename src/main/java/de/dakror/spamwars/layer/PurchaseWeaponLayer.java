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
import java.net.URL;

import org.json.JSONObject;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class PurchaseWeaponLayer extends MPLayer {
    WeaponData data;
    int costs;
    int id;

    public PurchaseWeaponLayer(WeaponData exisData, int id, WeaponData data) {
        modal = true;
        this.data = data;
        this.id = id;

        costs = data.getPrice();
        if (exisData != null) costs -= Math.round(exisData.getPrice() * 0.4f);
    }

    @Override
    public void draw(Graphics2D g) {
        drawModality(g);

        Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 125, 620, 250, true, false, g);
        Helper.drawHorizontallyCenteredString("Payment", Game.getWidth(), Game.getHeight() / 2 - 75, g, 40);

        Helper.drawHorizontallyCenteredString("Building your gun is gonna cost", Game.getWidth(), Game.getHeight() / 2 - 50, g, 20);

        Color c = g.getColor();
        if (Game.money >= costs) g.setColor(Color.darkGray);
        else g.setColor(Color.decode("#660a0a"));
        Helper.drawHorizontallyCenteredString(costs == 0 ? "Nothing" : costs + "$", Game.getWidth(), Game.getHeight() / 2 + 20, g, 80);
        g.setColor(c);

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
        TextButton cnc = new TextButton(Game.getWidth() / 2 - (int) (TextButton.WIDTH * (Game.money >= costs ? 1 : 0.5f)), Game.getHeight() / 2 + 30, "Cancel");
        cnc.addClickEvent(new ClickEvent() {
            @Override
            public void trigger() {
                Game.currentGame.removeLayer(PurchaseWeaponLayer.this);
            }
        });
        components.add(cnc);

        TextButton buy = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 30, "Pay");
        buy.addClickEvent(new ClickEvent() {
            @Override
            public void trigger() {
                try {
                    boolean success = false;
                    if (!CFG.INTERNET) {
                        JSONObject o = new JSONObject();
                        o.put("ID", (int) (Math.random() * 1000));
                        o.put("USERID", (int) (Math.random() * 1000));
                        o.put("WEAPONDATA", data.getSortedData().toString());

                        Game.weapons.put(o);
                        success = true;
                    } else if (Game.subMoney(costs)) {
                        String response = Helper.getURLContent(new URL("https://dakror.de/spamwars/api/weapons?username=" + Assistant.urlencode(Game.user.getUsername()) + "&token=" + Game.user.getToken() + "&addweapon=" + Assistant.urlencode(data.getSortedData().toString()) + "&setweapon=" + id)).trim();
                        success = response.contains("true");
                    }

                    final boolean s = success;
                    Game.currentGame.addLayer(new Alert("Your weapon was " + (success ? "" : " not") + " built successfully.", new ClickEvent() {
                        @Override
                        public void trigger() {
                            Game.currentGame.removeLayer(PurchaseWeaponLayer.this);
                            if (s) {
                                Game.currentGame.removeLayer(Game.currentGame.getActiveLayer());
                                Game.currentGame.removeLayer(Game.currentGame.getActiveLayer()); // BuildWeaponLayer
                                Game.currentGame.getActiveLayer().init();
                            }
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (Game.money >= costs) components.add(buy);
    }
}
