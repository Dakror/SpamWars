package de.dakror.spamwars.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.net.InetAddress;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.Spinner;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.weapon.WeaponData;
import de.dakror.spamwars.game.world.World;
import de.dakror.spamwars.layer.GameStartLayer;
import de.dakror.spamwars.layer.HUDLayer;
import de.dakror.spamwars.net.Client;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Game extends GameFrame implements WindowFocusListener
{
	public static World world;
	public static Game currentGame;
	public static Client client;
	public static Server server;
	public static InetAddress ip;
	public static User user;
	public static String passwordMD5;
	public static Player player;
	public static JSONArray weapons = new JSONArray();
	public static WeaponData activeWeapon;
	public static int money = 0;
	
	boolean debug = false;
	
	public Game()
	{
		super();
		currentGame = this;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (world != null) world.draw(g);
		
		drawLayers(g);
		
		if (!(getActiveLayer() instanceof HUDLayer) && !(getActiveLayer() instanceof GameStartLayer) && user != null)
		{
			Helper.drawContainer(getWidth() - 200, getHeight() - 60, 200, 60, false, false, g);
			g.setColor(Color.darkGray);
			Helper.drawRightAlignedString(money + "$", getWidth() - 10, getHeight() - 20, g, 25);
		}
		
		if (debug && !screenshot)
		{
			g.setColor(Color.green);
			g.setFont(new Font("Arial", Font.PLAIN, 18));
			Helper.drawString(getFPS() + " FPS", 0, 18, g, 18);
			Helper.drawString(getUPS() + " UPS", 100, 18, g, 18);
		}
	}
	
	@Override
	public void initGame()
	{
		w.addWindowFocusListener(this);
		w.setFocusTraversalKeysEnabled(false);
		w.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (client != null)
				{
					client.disconnect();
				}
			}
		});
		try
		{
			Spinner.h = 33;
			InputField.h = 8;
			w.setFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/SANDBOXB.ttf")));
			w.setIconImage(getImage("icon/spamwars64.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		w.setBackground(Color.decode("#D0F4F7"));
		
		client = new Client();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		if (world != null && !getActiveLayer().isModal()) world.keyPressed(e);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		super.keyReleased(e);
		
		if (e.getKeyCode() == KeyEvent.VK_F11) debug = !debug;
		
		if (world != null && !getActiveLayer().isModal()) world.keyReleased(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		if (world != null && !getActiveLayer().isModal()) world.mouseMoved(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		if (world != null && !getActiveLayer().isModal())
		{
			if (new Rectangle(5, 5, 70, 70).contains(e.getPoint())) return; // pause
			world.mousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		if (world != null && !getActiveLayer().isModal())
		{
			if (new Rectangle(5, 5, 70, 70).contains(e.getPoint())) return; // pause
			world.mouseReleased(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		super.mouseDragged(e);
		if (world != null && !getActiveLayer().isModal())
		{
			if (new Rectangle(5, 5, 70, 70).contains(e.getPoint())) return; // pause
			world.mouseDragged(e);
		}
	}
	
	@Override
	public void windowGainedFocus(WindowEvent e)
	{}
	
	@Override
	public void windowLostFocus(WindowEvent e)
	{
		if (player != null)
		{
			player.stop();
		}
	}
	
	public static void pullMoney()
	{
		if (!CFG.INTERNET) return;
		
		try
		{
			money = Integer.parseInt(Helper.getURLContent(new URL("http://dakror.de/spamwars/api/money?username=" + user.getUsername() + "&password=" + passwordMD5)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void pullWeapons()
	{
		
		// TODO: debug
		weapons = new JSONArray();
		JSONObject o = new JSONObject();
		try
		{
			o.put("ID", (int) (Math.random() * 1000));
			o.put("USERID", (int) (Math.random() * 1000));
			o.put("WEAPONDATA", "13:232.0:0.0;25:0.0:18.0;7:232.0:62.0;2:183.0:67.0;true");
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		weapons.put(o);
		
		
		
		if (!CFG.INTERNET) return;
		
		try
		{
			weapons = new JSONArray(Helper.getURLContent(new URL("http://dakror.de/spamwars/api/weapons?username=" + user.getUsername() + "&password=" + passwordMD5)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean subMoney(int money)
	{
		if (!CFG.INTERNET) return true;
		
		try
		{
			String response = Helper.getURLContent(new URL("http://dakror.de/spamwars/api/money?username=" + user.getUsername() + "&password=" + passwordMD5 + "&sub=" + money));
			if (!response.contains("false"))
			{
				Game.money = Integer.parseInt(response);
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
