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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet03Attribute;
import de.dakror.spamwars.net.packet.Packet04PlayerList;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.net.packet.Packet11GameInfo.GameMode;
import de.dakror.spamwars.ui.KillLabel;

/**
 * @author Dakror
 */
public class HUDLayer extends MPLayer {
    boolean showStats;
    public boolean reload;
    public int reloadStarted;
    int tick;
    int killY;

    BufferedImage stats;
    boolean invokeRenderStats = false;
    boolean started = false;

    @Override
    public void draw(Graphics2D g) {
        try {
            Helper.drawContainer(Game.getWidth() / 2 - 200, Game.getHeight() - 50, 400, 60, false, false, g);
            Helper.drawProgressBar(Game.getWidth() / 2 - 180, Game.getHeight() - 30, 360, Game.player.getLife() / (float) Game.player.getMaxlife(), "ff3232", g);
            Color o = g.getColor();
            g.setColor(Color.black);
            Helper.drawHorizontallyCenteredString(Game.player.getLife() + " / " + Game.player.getMaxlife(), Game.getWidth(), Game.getHeight() - 14, g, 14);

            Helper.drawContainer(Game.getWidth() - 175, Game.getHeight() - 110, 175, 110, false, false, g);
            g.setColor(Color.white);
            Helper.drawString(Game.player.getWeapon().ammo + "", Game.getWidth() - 165, Game.getHeight() - 50, g, 70);
            Helper.drawRightAlignedString(Game.player.getWeapon().capacity + "", Game.getWidth() - 10, Game.getHeight() - 15, g, 40);

            // -- time panel -- //
            Helper.drawContainer(Game.getWidth() / 2 - 150, 0, 300, 80, true, true, g);
            Helper.drawHorizontallyCenteredString(Game.client.isGameOver() ? "00:00" : new SimpleDateFormat("mm:ss").format(new Date((Game.client.gameStarted + Game.client.gameInfo.getMinutes() * 60000) - System.currentTimeMillis())), Game.getWidth(), 56, g, 50);

            if (!new Rectangle(5, 5, 70, 70).contains(Game.currentGame.mouse) || !Game.currentGame.getActiveLayer().equals(this)) Helper.drawContainer(5, 5, 70, 70, false, false, g);
            else Helper.drawContainer(0, 0, 80, 80, false, true, g);
            g.drawImage(Game.getImage("gui/pause.png"), 5, 5, 70, 70, Game.w);

            if (reload && reloadStarted > 0) {
                Composite c = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                Helper.drawShadow(Game.getWidth() / 2 - 260, Game.getHeight() / 3 * 2 - 10, 520, 40, g);
                Helper.drawOutline(Game.getWidth() / 2 - 260, Game.getHeight() / 3 * 2 - 10, 520, 40, false, g);
                Helper.drawProgressBar(Game.getWidth() / 2 - 251, Game.getHeight() / 3 * 2 - 1, 500, (tick - reloadStarted) / (float) Game.player.getWeapon().getData().getReload(), "2a86e7", g);
                g.setColor(Color.black);
                Helper.drawHorizontallyCenteredString("Reload", Game.getWidth(), Game.getHeight() / 3 * 2 + 16, g, 20);
                g.setComposite(c);
            }
            g.setColor(o);
        } catch (NullPointerException e) {}

        drawComponents(g);

        drawStats(g);
    }

    public void drawStats(Graphics2D g) {
        if (stats == null || invokeRenderStats) {
            stats = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g1 = (Graphics2D) stats.getGraphics();
            g1.setRenderingHints(g.getRenderingHints());
            g1.setFont(g.getFont());
            renderStats(g1);
            invokeRenderStats = false;
        }

        if (!showStats) return;
        g.drawImage(stats, 0, 0, Game.w);
    }

    public void renderStats(Graphics2D g) {
        Helper.drawContainer(Game.getWidth() / 2 - 500, Game.getHeight() / 2 - 300, 1000, 600, true, false, g);
        Color o = g.getColor();
        g.setColor(Color.gray);
        Helper.drawHorizontallyCenteredString("Stats", Game.getWidth(), Game.getHeight() / 2 - 220, g, 80);
        Helper.drawOutline(Game.getWidth() / 2 - 495, Game.getHeight() / 2 - 295, 990, 100, false, g);
        User[] users = Game.client.playerList.getUsers();
        Arrays.sort(users, getSorter());
        Helper.drawString("PLAYER NAME", Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 160, g, 30);
        Helper.drawString("K / D", Game.getWidth() / 2 + 300, Game.getHeight() / 2 - 160, g, 30);

        for (int i = 0; i < users.length; i++) {
            g.setColor(Color.white);
            if (users[i].getUsername().equals(Game.user.getUsername())) g.setColor(Color.decode("#3333ff"));
            Helper.drawString(users[i].getUsername(), Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 110 + i * 30, g, 30);
            Helper.drawString("/", Game.getWidth() / 2 + 350, Game.getHeight() / 2 - 110 + i * 30, g, 30);
            Helper.drawRightAlignedString(users[i].K + "", Game.getWidth() / 2 + 331, Game.getHeight() / 2 - 110 + i * 30, g, 30);
            Helper.drawString(users[i].D + "", Game.getWidth() / 2 + 391, Game.getHeight() / 2 - 110 + i * 30, g, 30);
        }
        g.setColor(o);
    }

    @Override
    public void update(int tick) {
        this.tick = tick;

        if (Game.activeWeapon == null || Game.player.getWeapon() == null) return;

        if (Game.server != null && !started) {
            Game.server.updater.time2 = 0;
            Game.server.updater.countdown = 5;
            started = true;
        }

        if (reload) {
            if (reloadStarted == 0) reloadStarted = tick;

            if (tick - reloadStarted >= Game.player.getWeapon().getData().getReload()) {
                Game.player.getWeapon().reload();
                reload = false;
            }
        }

        Game.player.getWeapon().reloading = reload;

        killY = 30;

        int killed = 0;

        for (Component c : components) {
            if (c instanceof KillLabel) {
                if (((KillLabel) c).dead) {
                    killed++;
                    components.remove(c);
                } else {
                    c.y -= killed * KillLabel.SPACE;

                    if (c.y + KillLabel.SPACE > killY) killY = c.y + KillLabel.SPACE;
                }
            }
        }

        if (Game.client.isGameOver() && !(Game.currentGame.getActiveLayer() instanceof WinnerLayer)) {
            Game.currentGame.addLayer(new WinnerLayer());
        }

        updateComponents(tick);
    }

    @Override
    public void onPacketReceived(Packet p, InetAddress ip, int port) {
        if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("countdown")) {
            Game.currentGame.addLayer(new GameStartLayer(false));
        }
        if (p instanceof Packet04PlayerList) invokeRenderStats = true;
        if (p instanceof Packet09Kill) {
            Player killer = null;
            Player killed = null;
            for (Entity e : Game.world.entities) {
                if (e instanceof Player) {
                    if (((Player) e).getUser().getUsername().equals(((Packet09Kill) p).getKiller())) killer = (Player) e;
                    if (((Player) e).getUser().getUsername().equals(((Packet09Kill) p).getDead())) killed = (Player) e;
                }
            }
            if (killer != null && killed != null) components.add(new KillLabel(killY, killer, killed, ((Packet09Kill) p).getWeapon()));

            if (((Packet09Kill) p).getKiller().equals(Game.user.getUsername())) {
                Game.subMoney(-25);
            }

            if (((Packet09Kill) p).getKiller().equals(Game.user.getUsername()) && Game.client.gameInfo.getGameMode() == GameMode.ONE_IN_THE_CHAMBER) Game.player.getWeapon().ammo = 1;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !(Game.currentGame.getActiveLayer() instanceof GameStartLayer)) Game.currentGame.addLayer(new PauseLayer());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);

        if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (new Rectangle(5, 5, 70, 70).contains(e.getPoint()) && !(Game.currentGame.getActiveLayer() instanceof GameStartLayer)) Game.currentGame.addLayer(new PauseLayer());
        if (e.getButton() == MouseEvent.BUTTON3 && Game.player.getWeapon().canReload() && !reload) {
            reload = true;
            reloadStarted = 0;
        }
    }

    @Override
    public void init() {
        showStats = false;
    }

    public static Comparator<User> getSorter() {
        return new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int K1 = o1.K, K2 = o2.K, D1 = o1.D, D2 = o2.D;

                if (D1 == 3 && Game.client.gameInfo.getGameMode() == GameMode.ONE_IN_THE_CHAMBER) return 1;
                if (D2 == 3 && Game.client.gameInfo.getGameMode() == GameMode.ONE_IN_THE_CHAMBER) return -1;

                if (K1 == 0 && K2 == 0) return Integer.compare(D1, D2);

                if (D1 == 0) D1++;
                if (D2 == 0) D2++;

                return Float.compare(K2 / (float) D2, K1 / (float) D1);
            }
        };
    }
}
