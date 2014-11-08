package de.dakror.spamwars.layer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import de.dakror.dakrorbin.DakrorBin;
import de.dakror.gamesetup.layer.Alert;
import de.dakror.gamesetup.ui.ClickEvent;
import de.dakror.gamesetup.ui.button.TextButton;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet13Server;
import de.dakror.spamwars.net.packet.Packet14Discovery;

/**
 * @author Dakror
 */
public class JoinLayer extends MPLayer
{
	Packet14Discovery discoveryPacket;
	
	HashMap<InetAddress, Packet13Server> servers = new HashMap<>();
	int selected = -1;
	int hovered;
	InetAddress hoveredIp;
	boolean gotoLobby;
	
	Point mouse = new Point();
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/joinGame.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		Helper.drawContainer(Game.getWidth() / 2 - TextButton.WIDTH - 15, Game.getHeight() - TextButton.HEIGHT * 2 - 30, TextButton.WIDTH * 2 + 30, TextButton.HEIGHT * 3, false, false, g);
		
		Rectangle rect = new Rectangle();
		
		synchronized (servers)
		{
			int i = 0;
			boolean anyoneHovered = false;
			for (InetAddress key : servers.keySet())
			{
				int y = Game.getHeight() / 4 + i * (TextButton.HEIGHT + 50);
				rect.setBounds(Game.getWidth() / 4, y, Game.getWidth() / 2, TextButton.HEIGHT + 10);
				
				boolean hovered = rect.contains(mouse);
				if (hovered)
				{
					this.hovered = i;
					anyoneHovered = true;
					hoveredIp = key;
				}
				
				Helper.drawContainer(Game.getWidth() / 4, y, Game.getWidth() / 2, TextButton.HEIGHT + 10, hovered, selected == i, g);
				
				Packet13Server p = servers.get(key);
				
				Helper.drawString(p.getHostName(), Game.getWidth() / 4 + 20, y + 45, g, 30);
				Helper.drawRightAlignedString(p.getPlayers() + " / " + Server.MAX_PLAYERS + " Spieler", Game.getWidth() / 4 * 3 - 20, y + 45, g, 30);
				
				i++;
			}
			
			if (!anyoneHovered) hovered = -1;
		};
		
		drawComponents(g);
	}
	
	public void request()
	{
		try
		{
			Game.client.broadCast(discoveryPacket);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (tick % 300 == 0) request();
		
		if (Game.currentFrame.alpha == 1 && enabled)
		{
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread()
			{
				@Override
				public void run()
				{
					Game.currentFrame.removeLayer(JoinLayer.this);
					if (gotoLobby) Game.currentFrame.addLayer(MenuLayer.ll);
				}
			}.start();
		}
		
		components.get(2).enabled = selected > -1;
	}
	
	@Override
	public void onPacketReceived(Packet p, InetAddress ip, int port)
	{
		if (p instanceof Packet00Connect && ((Packet00Connect) p).getUsername().equals(Game.user.getUsername()))
		{
			gotoLobby = true;
			Game.currentFrame.fadeTo(1, 0.05f);
		}
		else if (p instanceof Packet02Reject)
		{
			Game.currentGame.addLayer(new Alert(((Packet02Reject) p).getCause().getDescription(), null));
		}
		else if (p instanceof Packet13Server)
		{
			int value = ((Packet13Server) p).getPlayers();
			if (value >= 0) servers.put(ip, (Packet13Server) p); // if existing, updates value
			else servers.remove(ip);
		}
		else MenuLayer.ll.onPacketReceived(p, ip, port);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		mouse = e.getPoint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		
		if (hovered > -1 && e.getButton() == MouseEvent.BUTTON1) selected = hovered;
		else selected = -1;
	}
	
	@Override
	public void init()
	{
		discoveryPacket = new Packet14Discovery(DakrorBin.buildTimestamp);
		request();
		
		TextButton back = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH, Game.getHeight() - TextButton.HEIGHT - 10, "Zurück");
		back.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(back);
		TextButton update = new TextButton(Game.getWidth() / 2 - TextButton.WIDTH / 2, Game.getHeight() - TextButton.HEIGHT * 2 - 10, "Suchen");
		update.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				request();
			}
		});
		components.add(update);
		
		TextButton join = new TextButton(Game.getWidth() / 2, Game.getHeight() - TextButton.HEIGHT - 10, "Betreten");
		join.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.client.connectToServer(hoveredIp);
			}
		});
		join.enabled = false;
		components.add(join);
	}
}
