package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.net.URL;
import java.security.MessageDigest;

import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class LoginLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 150, 620, 300, true, false, g);
		Helper.drawHorizontallyCenteredString("Anmelden", Game.getWidth(), Game.getHeight() / 2 - 100, g, 40);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void init()
	{
		final InputField usr = new InputField(Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 80, 580, 30);
		usr.setHint("Benutzername");
		usr.setMaxlength(50);
		components.add(usr);
		
		final InputField pwd = new InputField(Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 20, 580, 30);
		pwd.setPassword(true);
		pwd.setMaxlength(50);
		String allowed = pwd.getAllowed();
		allowed += ",;.:-_#'+*~!§$%&/()=?<>| ";
		pwd.setAllowed(allowed);
		components.add(pwd);
		
		TextButton cnc = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 2 + 60, "Abbruch");
		cnc.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				System.exit(0);
			}
		});
		components.add(cnc);
		
		TextButton lgn = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 60, "Anmelden");
		lgn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				try
				{
					String pw = new String(HexBin.encode(MessageDigest.getInstance("MD5").digest(new String(pwd.getText()).getBytes()))).toLowerCase();
					String result = Helper.getURLContent(new URL("http://dakror.de/mp-api/login?username=" + usr.getText() + "&password=" + pw + "&ip=" + Game.ip.getHostAddress()));
					if (result.equals("faillogin")) Game.currentGame.addLayer(new Alert("Anmeldung fehlgeschlagen!", null));
					else if (result.startsWith("true"))
					{
						Game.user = new User(new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?id=" + result.substring(result.indexOf(":") + 1)))).getString("USERNAME"), null, 0);
						
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
	
	@Override
	public void onPacketReceived(Packet p)
	{}
}
