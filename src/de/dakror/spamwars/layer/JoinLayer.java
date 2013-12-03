package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

/**
 * @author Dakror
 */
public class JoinLayer extends MPLayer
{
	JFrame join;
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet00Connect && ((Packet00Connect) p).getUsername().equals(Game.user.getUsername()))
		{
			join.dispose();
			Game.currentGame.removeLayer(JoinLayer.this);
			Game.currentFrame.fadeTo(1, 0.05f);
		}
		if (p instanceof Packet02Reject)
		{
			JOptionPane.showMessageDialog(join, ((Packet02Reject) p).getCause().getDescription(), "Konnte Spiel nicht beitreten", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void init()
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
				Game.currentGame.removeLayer(JoinLayer.this);
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
}
