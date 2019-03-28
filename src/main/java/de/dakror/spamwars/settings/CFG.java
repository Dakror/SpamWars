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

package de.dakror.spamwars.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.util.Assistant;

/**
 * @author Dakror
 */
public class CFG {
    public static final File DIR = new File(System.getProperty("user.home") + "/.dakror/SpamWars");

    public static boolean INTERNET;

    static long time = 0;

    static InetAddress broadCastAddress;
    static InetAddress address;

    public static long VERSION = 16300923;

    public static final String[] initUsername(boolean force) {
        File us = new File(DIR, "username");
        String[] st = null;
        if (!us.exists() || us.length() == 0 || force) {
            String user = JOptionPane.showInputDialog((force ? "This name is taken already, please pick a different one." : "Please enter your nickname of choice."));

            if (user == null) System.exit(0);

            try {
                us.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String token = Assistant.getSha1Hex(System.nanoTime() + user);

            Helper.setFileContent(us, user + "\n" + token);
            st = new String[] { user, token };
        } else {
            String s = Helper.getFileContent(us);
            st = s.split("\r\n");
        }

        try {
            if (Helper.getURLContent(new URL("https://dakror.de/spamwars/api/money?username=" + Assistant.urlencode(st[0]) + "&token=" + st[1])).trim().equals("taken")) {
                return initUsername(true);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return st;
    }

    public static final void init() {
        DIR.mkdirs();
        GameFrame.screenshotDir = new File(DIR, "screenshots");

        File maps = new File(DIR, "maps");
        maps.mkdir();
        String[] files = { "BigSnowSurfaceStoneBasementDefault.map", "SmallGrassSurfaceStoneBasementDefault.map" };

        for (String s : files)
            Helper.setFileContent(new File(maps, s), Helper.getURLContent(CFG.class.getResource("/map/" + s)));

        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface ni : nis) {
                if (!ni.getInetAddresses().hasMoreElements()) continue;

                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    if (address != null) break;
                    if (ia.getAddress().isLoopbackAddress() || ia.getBroadcast() == null) continue;
                    broadCastAddress = ia.getBroadcast();
                    address = ia.getAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File ip = new File(DIR, "ip");
        try {
            if (!ip.exists() || ip.length() == 0) {
                ip.createNewFile();
                Helper.setFileContent(ip, address.getHostAddress());
            } else if (CFG.INTERNET) {
                address = InetAddress.getByName(Helper.getFileContent(ip).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InetAddress getBroadcastAddress() {
        return broadCastAddress;
    }

    public static InetAddress getAddress() {
        return address;
    }

    // -- user settings -- //
    public static boolean AUTO_RELOAD = true;

    public static void saveSettings() {
        File s = new File(DIR, ".properties");
        try {
            s.createNewFile();
            Properties properties = new Properties();
            properties.setProperty("AUTO_RELOAD", Boolean.toString(AUTO_RELOAD));
            properties.store(new FileWriter(s), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSettings() {
        File s = new File(DIR, ".properties");
        if (s.exists()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileReader(s));

                AUTO_RELOAD = properties.getProperty("AUTO_RELOAD").equals("true");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // -- debug profiling -- //
    public static void u() {
        if (time == 0) time = System.currentTimeMillis();
        else {
            p(System.currentTimeMillis() - time);
            time = 0;
        }
    }

    public static void p(Object... p) {
        if (p.length == 1) System.out.println(p[0]);
        else System.out.println(Arrays.toString(p));
    }
}
