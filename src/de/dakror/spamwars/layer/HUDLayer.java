package de.dakror.spamwars.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet04ServerInfo;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.ui.KillLabel;

/**
 * @author Dakror
 */
public class HUDLayer extends MPLayer
{
	boolean showStats;
	public boolean reload;
	public int reloadStarted;
	int tick;
	int killY;
	
	BufferedImage stats;
	boolean invokeRenderStats = false;
	
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
		
		Helper.drawContainer(Game.getWidth() - 175, Game.getHeight() - 110, 175, 110, false, false, g);
		g.setColor(Color.white);
		Helper.drawString(Game.player.getWeapon().ammo + "", Game.getWidth() - 165, Game.getHeight() - 50, g, 70);
		Helper.drawRightAlignedString(Game.player.getWeapon().capacity + "", Game.getWidth() - 10, Game.getHeight() - 15, g, 40);
		
		if (!new Rectangle(5, 5, 70, 70).contains(Game.currentGame.mouse) || !Game.currentGame.getActiveLayer().equals(this)) Helper.drawContainer(5, 5, 70, 70, false, false, g);
		else Helper.drawContainer(0, 0, 80, 80, false, true, g);
		g.drawImage(Game.getImage("gui/pause.png"), 5, 5, 70, 70, Game.w);
		
		if (reload)
		{
			Composite c = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			Helper.drawShadow(Game.getWidth() / 2 - 260, Game.getHeight() / 3 * 2 - 10, 520, 40, g);
			Helper.drawOutline(Game.getWidth() / 2 - 260, Game.getHeight() / 3 * 2 - 10, 520, 40, false, g);
			Helper.drawProgressBar(Game.getWidth() / 2 - 251, Game.getHeight() / 3 * 2 - 1, 500, (tick - reloadStarted) / (float) Game.player.getWeapon().reloadSpeed, "2a86e7", g);
			g.setColor(Color.black);
			Helper.drawHorizontallyCenteredString("Nachladen", Game.getWidth(), Game.getHeight() / 3 * 2 + 16, g, 20);
			g.setComposite(c);
		}
		
		g.setColor(o);
		
		drawComponents(g);
		
		drawStats(g);
	}
	
	public void drawStats(Graphics2D g)
	{
		if (stats == null || invokeRenderStats)
		{
			stats = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g1 = (Graphics2D) stats.getGraphics();
			g1.setRenderingHints(g.getRenderingHints());
			g1.setFont(g.getFont());
			renderStats(g1);
			invokeRenderStats = false;
		}
		
		if (!showStats) return;
		g.drawImage(stats, 0, 0, Game.w);
	}
	
	public void renderStats(Graphics2D g)
	{
		Helper.drawContainer(Game.getWidth() / 2 - 500, Game.getHeight() / 2 - 300, 1000, 600, true, false, g);
		Color o = g.getColor();
		g.setColor(Color.gray);
		Helper.drawHorizontallyCenteredString("Statistik", Game.getWidth(), Game.getHeight() / 2 - 220, g, 80);
		Helper.drawOutline(Game.getWidth() / 2 - 495, Game.getHeight() / 2 - 295, 990, 100, false, g);
		User[] users = Game.client.serverInfo.getUsers();
		Arrays.sort(users, new Comparator<User>()
		{
			@Override
			public int compare(User o1, User o2)
			{
				if (o1.D == 0 && o2.D == 0) return 0;
				if (o1.D == 0) return -1;
				if (o2.D == 0) return 1;
				int compare = Float.compare(o2.K / (float) o2.D, o1.K / (float) o1.D);
				return compare;
			}
		});
		Helper.drawString("SPIELERNAME", Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 160, g, 30);
		Helper.drawString("K / D", Game.getWidth() / 2 + 300, Game.getHeight() / 2 - 160, g, 30);
		
		for (int i = 0; i < users.length; i++)
		{
			g.setColor(Color.white);
			if (users[i].getUsername().equals(Game.user.getUsername())) g.setColor(Color.decode("#3333ff"));
			Helper.drawString(users[i].getUsername(), Game.getWidth() / 2 - 450, Game.getHeight() / 2 - 110 + i * 30, g, 30);
			Helper.drawString("/", Game.getWidth() / 2 + 340, Game.getHeight() / 2 - 110 + i * 30, g, 30);
			Helper.drawRightAlignedString(users[i].K + "", Game.getWidth() / 2 + 325, Game.getHeight() / 2 - 110 + i * 30, g, 30);
			Helper.drawString(users[i].D + "", Game.getWidth() / 2 + 382, Game.getHeight() / 2 - 110 + i * 30, g, 30);
		}
		g.setColor(o);
	}
	
	@Override
	public void update(int tick)
	{
		this.tick = tick;
		
		if (reload)
		{
			if (reloadStarted == 0) reloadStarted = tick;
			
			if (tick - reloadStarted >= Game.player.getWeapon().reloadSpeed)
			{
				Game.player.getWeapon().reload();
				reload = false;
			}
		}
		
		Game.player.getWeapon().reloading = reload;
		
		killY = 30;
		
		int killed = 0;
		
		for (Component c : components)
		{
			if (c instanceof KillLabel)
			{
				if (((KillLabel) c).dead)
				{
					killed++;
					components.remove(c);
				}
				else
				{
					c.y -= killed * KillLabel.SPACE;
					
					if (c.y + KillLabel.SPACE > killY) killY = c.y + KillLabel.SPACE;
				}
			}
		}
		
		updateComponents(tick);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet09Kill) components.add(new KillLabel(killY, ((Packet09Kill) p).getKiller(), ((Packet09Kill) p).getDead(), ((Packet09Kill) p).getWeapon()));
		if (p instanceof Packet04ServerInfo) invokeRenderStats = true;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		
		if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = true;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) Game.currentGame.addLayer(new PauseLayer());
		if (e.getKeyCode() == KeyEvent.VK_R && Game.player.getWeapon().canReload() && !reload)
		{
			reload = true;
			reloadStarted = 0;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		super.keyReleased(e);
		
		if (e.getKeyCode() == KeyEvent.VK_TAB) showStats = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		
		if (new Rectangle(5, 5, 70, 70).contains(e.getPoint())) Game.currentGame.addLayer(new PauseLayer());
	}
	
	@Override
	public void init()
	{
		showStats = false;
	}
}
