package de.dakror.spamwars.game.layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.ui.ClickEvent;
import de.dakror.spamwars.game.ui.MenuButton;

/**
 * @author Dakror
 */
public class MenuLayer extends Layer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/title.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
		
		drawComponents(g);
	}
	
	@Override
	public void update(int tick)
	{
		updateComponents(tick);
		if (Game.currentFrame.alpha == 1)
		{
			Game.currentFrame.layers.remove(this);
			Game.currentFrame.addLayer(new LobbyLayer());
			Game.currentFrame.fadeTo(0, 0.05f);
		}
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
		MenuButton join = new MenuButton("joinGame", 1);
		join.addClickEvent(new ClickEvent()
		{
			@Override
			public void trigger()
			{
				final JFrame frame = new JFrame();
				frame.setAlwaysOnTop(true);
				frame.setResizable(false);
				frame.setUndecorated(true);
				frame.setSize(350, 100);
				frame.setLocationRelativeTo(Game.w);
				frame.setBackground(new Color(0, 0, 0, 0));
				// frame.addWindowFocusListener(new WindowFocusListener()
				// {
				//
				// @Override
				// public void windowLostFocus(WindowEvent e)
				// {
				// frame.dispose();
				// }
				//
				// @Override
				// public void windowGainedFocus(WindowEvent e)
				// {}
				// });
				
				JPanel p = new JPanel(new FlowLayout());
				p.setBackground(new Color(0, 0, 0, 0.8f));
				
				JLabel l = new JLabel("<html><center>Gib den Dakror.de - Benutzernamen des Spielers ein,<br>dessen Spiel du beitreten m&ouml;chtest</center></html>");
				l.setForeground(Color.white);
				p.add(l);
				
				final JTextField usr = new JTextField();
				usr.setPreferredSize(new Dimension(frame.getWidth() - 40, 22));
				p.add(usr);
				
				JButton cnc = new JButton(new AbstractAction("Abbruch")
				{
					
					private static final long serialVersionUID = 1L;
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						frame.dispose();
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
							try
							{
								JSONObject data = new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?name=" + usr.getText())));
								if (data.length() == 0)
								{
									JOptionPane.showMessageDialog(null, "Der Spieler, dessen Spiel du beitreten willst, konnte nicht gefunden werden.", "Beitreten nicht möglich", JOptionPane.ERROR_MESSAGE);
								}
								
								Game.client.connectToServer(InetAddress.getByName(data.getString("IP")));
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
						
						frame.toFront();
					}
				});
				go.setPreferredSize(new Dimension(usr.getPreferredSize().width / 2, usr.getPreferredSize().height));
				go.setBackground(new Color(0, 0, 0, 0));
				
				p.add(go);
				
				frame.setContentPane(p);
				
				frame.setVisible(true);
				// Game.currentGame.initWorld();
				//
				// Game.currentFrame.layers.remove(MenuLayer.this);
			}
		});
		components.add(join);
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
}
