package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONObject;

import de.dakror.gamesetup.GameFrame;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.InputField;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class JoinLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		
		Helper.drawContainer(GameFrame.getWidth() / 2 - 310, Game.getHeight() / 2 - 125, 620, 250, true, false, g);
		Helper.drawHorizontallyCenteredString("Beitreten" + (!CFG.INTERNET ? " (LAN-IP)" : ""), Game.getWidth(), Game.getHeight() / 2 - 75, g, 40);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet00Connect && ((Packet00Connect) p).getUsername().equals(Game.user.getUsername()))
		{
			Game.currentGame.removeLayer(JoinLayer.this);
			Game.currentFrame.fadeTo(1, 0.05f);
		}
		if (p instanceof Packet02Reject)
		{
			Game.currentGame.addLayer(new Alert(((Packet02Reject) p).getCause().getDescription(), null));
		}
	}
	
	@Override
	public void init()
	{
		final InputField usr = new InputField(Game.getWidth() / 2 - 290, Game.getHeight() / 2 - 50, 580, 30);
		usr.setHint("Benutzername");
		usr.setMaxlength(50);
		components.add(usr);
		
		TextButton cnc = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 2 + 30, "Abbruch");
		cnc.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.removeLayer(JoinLayer.this);
			}
		});
		components.add(cnc);
		
		TextButton lgn = new TextButton(Game.getWidth() / 2, Game.getHeight() / 2 + 30, "Beitreten");
		lgn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				if (usr.getText().length() > 0)
				{
					if (CFG.INTERNET)
					{
						try
						{
							JSONObject data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?name=" + usr.getText())));
							if (data.length() == 0) Game.currentGame.addLayer(new Alert("Der Spieler, dessen Spiel du beitreten willst, konnte nicht gefunden werden.", null));
							
							Game.client.connectToServer(InetAddress.getByName(data.getString("IP")));
						}
						catch (Exception e1)
						{}
					}
					else
					{
						try
						{
							Game.client.connectToServer(InetAddress.getByName(usr.getText()));
						}
						catch (UnknownHostException e1)
						{
							Game.currentGame.addLayer(new Alert("Die eingegebene IP-Adresse ist nicht gültig.", null));
						}
					}
				}
			}
		});
		components.add(lgn);
	}
}
