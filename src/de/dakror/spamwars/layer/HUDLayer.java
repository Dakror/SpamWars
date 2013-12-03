package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class HUDLayer extends MPLayer
{
	boolean showStats;
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawContainer(Game.getWidth() / 2 - 200, Game.getHeight() - 50, 400, 60, false, false, g);
		Helper.drawProgressBar(Game.getWidth() / 2 - 180, Game.getHeight() - 30, 360, Game.player.getLife() / (float) Game.player.getMaxlife(), "ff3232", g);
		Font old = g.getFont();
		g.setFont(new Font("Arial", Font.PLAIN, 25));
		Color o = g.getColor();
		g.setColor(Color.black);
		Helper.drawHorizontallyCenteredString(Game.player.getLife() + " / " + Game.player.getMaxlife(), Game.getWidth(), Game.getHeight() - 14, g, 14);
		g.setFont(old);
		g.setColor(o);
		
		drawComponents(g);
		
		drawStats(g);
	}
	
	public void drawStats(Graphics2D g)
	{
		if (!showStats) return;
		
		Helper.drawContainer(Game.getWidth() / 2 - 500, Game.getHeight() / 2 - 300, 1000, 600, true, false, g);
		Color o = g.getColor();
		g.setColor(Color.gray);
		Helper.drawHorizontallyCenteredString("Statistik", Game.getWidth(), Game.getHeight() / 2 - 220, g, 80);
		Helper.drawOutline(Game.getWidth() / 2 - 495, Game.getHeight() / 2 - 295, 990, 100, false, g);
		User[] users = Game.client.serverInfo.getUsers();
		Helper.drawString("SPIELER", Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 160, g, 30);
		Helper.drawString("K / D", Game.getWidth() / 2 + 300, Game.getHeight() / 2 - 160, g, 30);
		
		for (int i = 0; i < users.length; i++)
		{
			g.setColor(Color.white);
			if (users[i].getUsername().equals(Game.user.getUsername())) g.setColor(Color.decode("#3333ff"));
			Helper.drawString(users[i].getUsername(), Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 110 + i * 30, g, 30);
			Helper.drawString(users[i].K + " / " + users[i].D, Game.getWidth() / 2 + 300, Game.getHeight() / 2 - 110 + i * 30, g, 30);
		}
		g.setColor(o);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet09Kill)
		{
			CFG.p(((Packet09Kill) p).getKiller() + " -> " + ((Packet09Kill) p).getDead());
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		
		if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		super.keyReleased(e);
		
		if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = false;
	}
	
	@Override
	public void init()
	{
		showStats = false;
		// components.add(new KillLabel(50));
	}
}
