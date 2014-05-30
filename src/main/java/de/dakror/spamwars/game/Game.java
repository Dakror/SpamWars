package de.dakror.spamwars.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.JOptionPane;

import org.json.JSONArray;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.Spinner;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.game.world.World;
import de.dakror.spamwars.layer.ConnectingLayer;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.net.Networker;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Game extends GameFrame
{
	public static Game currentGame;
	public static Networker networker;
	public static WarpClient warp;
	public static World world;
	public static Player player;
	
	public static int money = 0;
	public static JSONArray weapons = new JSONArray();
	public static WeaponData activeWeapon;
	
	boolean debug = false;
	
	public Game()
	{
		super();
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		addLayer(new MenuLayer());
		addLayer(new ConnectingLayer());
		
		byte b = WarpClient.initialize(CFG.APP_KEY, "", CFG.SERVER_IP);
		if (b != 0)
		{
			JOptionPane.showMessageDialog(w, "Server konnte nicht erreicht werden!", "Fehler!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		try
		{
			warp = WarpClient.getInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		networker = new Networker();
		warp.addChatRequestListener(networker);
		warp.addConnectionRequestListener(networker);
		warp.addLobbyRequestListener(networker);
		warp.addNotificationListener(networker);
		warp.addRoomRequestListener(networker);
		warp.addUpdateRequestListener(networker);
		warp.addZoneRequestListener(networker);
		
		warp.initUDP();
		
		warp.connectWithUserName(Launch.username);
		
		pullMoney();
		pullWeapons();
		
		Spinner.h = 33;
		InputField.h = 8;
		try
		{
			w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/SANDBOXB.ttf")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		w.setIconImage(getImage("icon/spamwars64.png"));
		w.setBackground(Color.decode("#D0F4F7"));
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawLayers(g);
		
		// if (!(getActiveLayer() instanceof HUDLayer) && !(getActiveLayer() instanceof GameStartLayer))
		// {
		Helper.drawContainer(getWidth() - 200, getHeight() - 60, 200, 60, false, false, g);
		g.setColor(Color.darkGray);
		Helper.drawRightAlignedString(money + "$", getWidth() - 10, getHeight() - 20, g, 25);
		// }
		
		if (debug && !screenshot)
		{
			g.setColor(Color.green);
			g.setFont(new Font("Arial", Font.PLAIN, 18));
			Helper.drawString(getFPS() + " FPS", 0, 18, g, 18);
			Helper.drawString(getUPS() + " UPS", 100, 18, g, 18);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		super.keyReleased(e);
		
		if (e.getKeyCode() == KeyEvent.VK_F11) debug = !debug;
	}
	
	public static void pullMoney()
	{
		try
		{
			money = Integer.parseInt(Helper.getURLContent(new URL("http://dakror.de/spamwars/api/money?username=" + Launch.username + "&password=" + Launch.pwdMd5)).trim());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void pullWeapons()
	{
		try
		{
			weapons = new JSONArray(Helper.getURLContent(new URL("http://dakror.de/spamwars/api/weapons?username=" + Launch.username + "&password=" + Launch.pwdMd5)).trim());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean subMoney(int money)
	{
		try
		{
			String response = Helper.getURLContent(new URL("http://dakror.de/spamwars/api/money?username=" + Launch.username + "&password=" + Launch.pwdMd5 + "&sub=" + money));
			if (!response.contains("false"))
			{
				money = Integer.parseInt(response);
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
