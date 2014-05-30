package de.dakror.spamwars.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.net.URL;

import javax.swing.JOptionPane;

import org.json.JSONArray;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import de.dakror.dakrorbin.Launch;
import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.Spinner;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.layer.ConnectingLayer;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.net.Client;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Game extends GameFrame
{
	public static Game currentGame;
	public static Client client;
	public static WarpClient warp;
	
	public static int money = 0;
	public static JSONArray weapons = new JSONArray();
	public static WeaponData activeWeapon;
	
	public Game()
	{
		super();
		currentGame = this;
	}
	
	@Override
	public void initGame()
	{
		Game.currentGame.addLayer(new MenuLayer());
		Game.currentGame.addLayer(new ConnectingLayer());
		
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
		client = new Client();
		warp.addChatRequestListener(client);
		warp.addConnectionRequestListener(client);
		warp.addLobbyRequestListener(client);
		warp.addNotificationListener(client);
		warp.addRoomRequestListener(client);
		warp.addUpdateRequestListener(client);
		warp.addZoneRequestListener(client);
		
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
