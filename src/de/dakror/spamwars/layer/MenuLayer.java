package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.ui.ClickEvent;
import de.dakror.spamwars.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends MPLayer
{
	JFrame join;
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		if (!CFG.INTERNET)
		{
			Font old = g.getFont();
			g.setFont(new Font("", Font.PLAIN, 20));
			Helper.drawHorizontallyCenteredString("Offline-Modus: " + Game.user.getUsername() + " (" + Game.user.getIP().getHostAddress() + ")", Game.getWidth(), 20, g, 20);
			g.setFont(old);
		}
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1 && enabled)
		{
			if (join != null) join.dispose();
			Game.currentFrame.fadeTo(0, 0.05f);
			new Thread()
			{
				@Override
				public void run()
				{
					Game.currentFrame.addLayer(new LobbyLayer());
				}
			}.start();
		}
		
		enabled = Game.currentGame.getActiveLayer().equals(this);
	}
	
	@Override
	public void init()
	{
		MenuButton start = new MenuButton("startGame", 0);
		start.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				Game.currentFrame.fadeTo(1, 0.05f);
			}
		});
		components.add(start);
		MenuButton joingame = new MenuButton("joinGame", 1);
		joingame.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				join = new JFrame();
				join.setAlwaysOnTop(true);
				join.setResizable(false);
				join.setUndecorated(true);
				join.setSize(350, 100);
				join.setLocationRelativeTo(Game.w);
				join.setBackground(new Color(0, 0, 0, 0));
				join.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosed(WindowEvent e)
					{
						enabled = true;
					}
					
					@Override
					public void windowOpened(WindowEvent e)
					{
						enabled = false;
					}
				});
				
				JPanel p = new JPanel(new FlowLayout());
				p.setBackground(new Color(0, 0, 0, 0.8f));
				
				JLabel l = new JLabel("<html><center>Gib " + (CFG.INTERNET ? "den Dakror.de - Benutzernamen" : "die IP im LAN") + " des Spielers ein,<br>dessen Spiel du beitreten m&ouml;chtest</center></html>");
				l.setForeground(Color.white);
				p.add(l);
				
				final JTextField usr = new JTextField();
				usr.setPreferredSize(new Dimension(join.getWidth() - 40, 22));
				p.add(usr);
				
				JButton cnc = new JButton(new AbstractAction("Abbruch")
				{
					
					private static final long serialVersionUID = 1L;
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						join.dispose();
					}
				});
				cnc.setPreferredSize(new Dimension(usr.getPreferredSize().width / 2, usr.getPreferredSize().height));
				cnc.setBackground(new Color(0, 0, 0, 0));
				p.add(cnc);
				
				JButton go = new JButton(new AbstractAction("Spiel beitreten")
				{
					private static final long serialVersionUID = 1L;
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (usr.getText().length() > 0)
						{
							if (CFG.INTERNET)
							{
								try
								{
									JSONObject data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?name=" + usr.getText())));
									if (data.length() == 0)
									{
										JOptionPane.showMessageDialog(join, "Der Spieler, dessen Spiel du beitreten willst, konnte nicht gefunden werden.", "Beitreten nicht möglich", JOptionPane.ERROR_MESSAGE);
									}
									
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
									JOptionPane.showMessageDialog(join, "Die eingegebene IP-Adresse ist nicht gültig.", "IP-Adresse ungültig", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
						
						join.toFront();
					}
				});
				go.setPreferredSize(new Dimension(usr.getPreferredSize().width / 2, usr.getPreferredSize().height));
				go.setBackground(new Color(0, 0, 0, 0));
				
				p.add(go);
				
				join.setContentPane(p);
				
				join.setVisible(true);
			}
		});
		components.add(joingame);
		MenuButton opt = new MenuButton("options", 2);
		components.add(opt);
		MenuButton end = new MenuButton("endGame", 3);
		end.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				System.exit(0);
			}
		});
		components.add(end);
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet00Connect && ((Packet00Connect) p).getUsername().equals(Game.user.getUsername()))
		{
			Game.currentFrame.fadeTo(1, 0.05f);
		}
		if (p instanceof Packet02Reject)
		{
			JOptionPane.showMessageDialog(join == null ? Game.w : join, ((Packet02Reject) p).getCause().getDescription(), "Konnte Spiel nicht beitreten", JOptionPane.ERROR_MESSAGE);
		}
	}
}
