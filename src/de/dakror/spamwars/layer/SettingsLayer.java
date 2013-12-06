package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class SettingsLayer extends MPLayer
{
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{
		final JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setSize(350, 100);
		frame.setLocationRelativeTo(Game.w);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				Game.currentGame.removeLayer(SettingsLayer.this);
			}
		});
		
		JPanel p = new JPanel(new FlowLayout());
		p.setBackground(new Color(0, 0, 0, 0.8f));
		
		JLabel l = new JLabel("<html><center>Optionen</center></html>");
		l.setForeground(Color.white);
		p.add(l);
		
		final JCheckBox autoreload = new JCheckBox("Automatisches Nachladen");
		autoreload.setPreferredSize(new Dimension(300, 22));
		autoreload.setHorizontalAlignment(JCheckBox.CENTER);
		autoreload.setOpaque(false);
		autoreload.setFocusPainted(false);
		autoreload.setBackground(null);
		autoreload.setRolloverEnabled(false);
		autoreload.setForeground(Color.white);
		autoreload.setSelected(CFG.AUTO_RELOAD);
		p.add(autoreload);
		
		JButton cnc = new JButton(new AbstractAction("Abbruch")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});
		cnc.setPreferredSize(new Dimension(155, 22));
		cnc.setBackground(new Color(0, 0, 0, 0));
		p.add(cnc);
		
		JButton go = new JButton(new AbstractAction("Speichern")
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				CFG.AUTO_RELOAD = autoreload.isSelected();
				
				CFG.saveSettings();
				
				frame.dispose();
			}
		});
		go.setPreferredSize(new Dimension(155, 22));
		go.setBackground(new Color(0, 0, 0, 0));
		
		p.add(go);
		
		frame.setContentPane(p);
		
		frame.setVisible(true);
	}
}
