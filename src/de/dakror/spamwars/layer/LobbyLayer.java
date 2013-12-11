package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFileChooser;

import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.ArrowButton.ArrowType;
import de.dakror.gamesetup.ui.button.Spinner;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet03Attribute;
import de.dakror.spamwars.net.packet.Packet04PlayerList;
import de.dakror.spamwars.net.packet.Packet11GameInfo;
import de.dakror.spamwars.net.packet.Packet11GameInfo.GameMode;

/**
 * @author Dakror
 */
public class LobbyLayer extends MPLayer
{
	boolean fade;
	
	Spinner mode, time;
	
	CopyOnWriteArrayList<String> ready;
	
	TextButton start;
	
	public LobbyLayer()
	{
		ready = new CopyOnWriteArrayList<>();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/startGame.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawContainer(Game.getWidth() / 2 - 305, Game.getHeight() / 4 * 3 - 20, TextButton.WIDTH * 2 + 30, TextButton.HEIGHT * 2 + 40, false, false, g);
		
		
		Helper.drawContainer(0, 300, Game.getWidth() / 4, (Game.getHeight() / 4 * 3 - 20 + TextButton.HEIGHT + 40) - 300, false, false, g);
		Helper.drawHorizontallyCenteredString("Optionen", Game.getWidth() / 4, 340, g, 28);
		
		Helper.drawHorizontallyCenteredString("Spielmodus", Game.getWidth() / 4, 480, g, 28);
		Helper.drawHorizontallyCenteredString("Zeit (min):", Game.getWidth() / 8, 580, g, 28);
		
		Color c = g.getColor();
		if (Game.client.playerList != null)
		{
			for (int i = 0; i < Game.client.playerList.getUsers().length; i++)
			{
				if (ready.contains(Game.client.playerList.getUsers()[i].getUsername())) g.setColor(Color.decode("#186b0d"));
				else g.setColor(Color.decode("#1c0d09"));
				
				Helper.drawHorizontallyCenteredString(Game.client.playerList.getUsers()[i].getUsername(), Game.getWidth(), 400 + i * 60, g, 60);
			}
		}
		g.setColor(c);
		
		drawComponents(g);
		
		if (Game.currentGame.alpha == 1 && Game.world != null)
		{
			Game.world.render(Game.client.gameInfo.getGameMode());
			Game.currentGame.setLayer(new HUDLayer());
			
			Game.currentGame.fadeTo(0, 0.05f);
		}
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		
		if (Game.server == null && Game.client.gameInfo != null) // non-host
		{
			Spinner mode = (Spinner) components.get(1);
			mode.value = Game.client.gameInfo.getGameMode().ordinal();
			
			Spinner time = (Spinner) components.get(2);
			time.value = Game.client.gameInfo.getMinutes();
			
			components.get(4).enabled = Game.activeWeapon != null; // ready button
		}
		else if (Game.server != null)
		{
			if (components.size() > 4) components.get(4).enabled = ready.size() == Game.client.playerList.getUsers().length && Game.activeWeapon != null; // start button
		}
	}
	
	@Override
	public void init()
	{
		
		final boolean host = !Game.client.isConnected() || Game.server != null;
		if (host && Game.server == null)
		{
			Game.server = new Server(Game.ip); // host
		}
		
		if (host) ready.add(Game.user.getUsername());
		
		TextButton map = new TextButton((Game.getWidth() / 4 - TextButton.WIDTH) / 2, 380, "Karte");
		map.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				JFileChooser jfc = new JFileChooser(Game.server.map);
				jfc.setSelectedFile(Game.server.map);
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setDialogTitle("Server-Karte auswählen");
				
				if (jfc.showOpenDialog(Game.w) == JFileChooser.APPROVE_OPTION) Game.server.map = jfc.getSelectedFile();
			}
		});
		map.enabled = host;
		components.add(map);
		
		mode = new Spinner(15, 490, Game.getWidth() / 4 - 30, 0, GameMode.values().length - 1, 1, 0, ArrowType.ARROW_L_HOR, ArrowType.ARROW_R_HOR);
		mode.enabled = host;
		mode.plus.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				sendInfo();
			}
		});
		mode.minus.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				sendInfo();
			}
		});
		for (GameMode g : GameMode.values())
		{
			mode.addAlias(g.ordinal(), g.getName());
		}
		components.add(mode);
		
		time = new Spinner(Game.getWidth() / 8, 545, Game.getWidth() / 8 - 15, 1, 30, 1, 5, ArrowType.MINUS_HOR, ArrowType.PLUS_HOR);
		time.enabled = host;
		time.plus.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				sendInfo();
			}
		});
		time.minus.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				sendInfo();
			}
		});
		components.add(time);
		
		TextButton wpn = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH / 2, Game.getHeight() / 4 * 3, "Waffe");
		wpn.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentGame.addLayer(new WeaponryLayer(false));
			}
		});
		components.add(wpn);
		
		start = new TextButton(Game.getWidth() / 2, Game.getHeight() / 4 * 3 + TextButton.HEIGHT, host ? "Start" : "Bereit");
		start.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				if (host)
				{
					Game.server.mode = GameMode.values()[mode.value];
					Game.server.minutes = time.value;
					Game.server.updater.countdown = 5;
				}
				else
				{
					try
					{
						Game.client.sendPacket(new Packet03Attribute("ready_" + Game.user.getUsername(), Boolean.toString(start.isSelected())));
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		if (!host) start.setToggleMode(true);
		components.add(start);
		
		if (!Game.client.isConnected()) Game.client.connectToServer(Game.ip);
		
		TextButton disco = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() / 4 * 3 + TextButton.HEIGHT, "Trennen");
		disco.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.client.disconnect();
				
				Game.currentGame.removeLayer(LobbyLayer.this);
			}
		});
		components.add(disco);
		
		try
		{
			Game.client.sendPacket(new Packet04PlayerList());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (host) sendInfo();
	}
	
	public void sendInfo()
	{
		try
		{
			Game.server.sendPacketToAllClientsExceptOne(new Packet11GameInfo(time.value, GameMode.values()[mode.value]), Game.user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet00Connect && Game.server != null)
		{
			sendInfo();
			try
			{
				Game.client.sendPacket(new Packet03Attribute("ready_" + Game.user.getUsername(), "true"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (p instanceof Packet00Connect && Game.server == null)
		{
			try
			{
				if (start != null) Game.client.sendPacket(new Packet03Attribute("ready_" + Game.user.getUsername(), Boolean.toString(start.isSelected())));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("worldsize")) Game.currentGame.fadeTo(1, 0.05f);
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("countdown")) Game.currentGame.addLayer(new GameStartLayer(true));
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().startsWith("ready_"))
		{
			String user = ((Packet03Attribute) p).getKey().replace("ready_", "");
			if (Boolean.parseBoolean(((Packet03Attribute) p).getValue()))
			{
				if (!ready.contains(user)) ready.add(user);
			}
			else ready.remove(user);
		}
		if (p instanceof Packet01Disconnect)
		{
			try
			{
				Game.client.sendPacket(new Packet04PlayerList());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
