package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Properties;

import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.Checkbox;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class LoginLayer extends MPLayer
{
	boolean loginAuto = false;
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 175, 620, 350, true, false, g);
		Helper.drawHorizontallyCenteredString("Anmelden", Game.getWidth(), Game.getHeight() / 2 - 125, g, 40);
		
		Helper.drawString("Anmeldung speichern:", GameFrame.getWidth() / 2 - 280, Game.getHeight() / 2 + 50, g, 25);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		
		if (loginAuto) Game.currentGame.removeLayer(this);
	}
	
	@Override
	public void init()
	{
		Properties login;
		if ((login = CFG.loadLogin()) != null)
		{
			if (login(login.getProperty("username"), login.getProperty("pwd")))
			{
				loginAuto = true;
				return;
			}
			else CFG.deleteLogin();
		}
		
		final InputField usr = new InputField(Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 100, 580, 30);
		usr.setHint("Benutzername");
		usr.setMaxlength(50);
		components.add(usr);
		
		final InputField pwd = new InputField(Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 40, 580, 30);
		pwd.setPassword(true);
		pwd.setMaxlength(50);
		String allowed = pwd.getAllowed();
		allowed += ",;.:-_#'+*~!§$%&/()=?<>| ";
		pwd.setAllowed(allowed);
		components.add(pwd);
		
		final Checkbox save = new Checkbox(Game.getWidth() / 2 + 240, Game.getHeight() / 2 + 25);
		components.add(save);
		
		TextButton cnc = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 2 + 80, "Abbruch");
		cnc.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				System.exit(0);
			}
		});
		components.add(cnc);
		
		TextButton lgn = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 80, "Anmelden");
		lgn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					String pw = new String(HexBin.encode(MessageDigest.getInstance("MD5").digest(new String(pwd.getText()).getBytes()))).toLowerCase();
					if (login(usr.getText(), pw))
					{
						if (save.isSelected()) CFG.saveLogin(usr.getText(), pw);
						else CFG.deleteLogin();
						
						Game.currentFrame.removeLayer(LoginLayer.this);
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		components.add(lgn);
	}
	
	public boolean login(String username, String passwordMD5)
	{
		try
		{
			String result = Helper.getURLContent(new URL("http://dakror.de/mp-api/login?username=" + username + "&password=" + passwordMD5 + "&ip=" + Game.ip.getHostAddress()));
			
			if (result == null || result.equals("faillogin")) Game.currentGame.addLayer(new Alert("Anmeldung fehlgeschlagen!", null));
			else if (result.startsWith("true"))
			{
				Game.user = new User(new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?id=" + result.substring(result.indexOf(":") + 1)))).getString("USERNAME"), null, 0);
				
				Game.passwordMD5 = passwordMD5;
				Game.pullMoney();
				Game.pullWeapons();
				
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Game.currentGame.addLayer(new Alert("Anmeldung fehlgeschlagen!", null));
		}
		return false;
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
}
