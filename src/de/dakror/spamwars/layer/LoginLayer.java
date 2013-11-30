package de.dakror.spamwars.layer;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.util.JHintTextField;

/**
 * @author Dakror
 */
public class LoginLayer extends Layer
{
	public LoginLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		final JFrame frame = new JFrame();
		frame.setSize(350, 150);
		frame.setLocationRelativeTo(Game.w);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setIgnoreRepaint(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel p = new JPanel(new FlowLayout());
		p.setBackground(new Color(0, 0, 0, 0.8f));
		JLabel label = new JLabel("<html><center>Bitte melde dich mit deinem Dakror.de Benutzerkonto an.<br>Wenn du noch keines hast, klicke auf diesen Text, um zur<br>Registration zu gelangen<br><br></center></html>");
		label.setForeground(Color.white);
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI("http://dakror.de/#register"));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		p.add(label);
		
		final JHintTextField usr = new JHintTextField("Benutzername");
		usr.setBackground(Color.white);
		usr.setPreferredSize(new Dimension(frame.getWidth() - 40, 20));
		p.add(usr);
		
		final JPasswordField pwd = new JPasswordField("Passwort");
		pwd.setPreferredSize(new Dimension(frame.getWidth() - 40, 20));
		pwd.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e)
			{}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				pwd.setText("");
			}
		});
		pwd.setBackground(Color.white);
		p.add(pwd);
		
		JButton login = new JButton("Anmelden");
		login.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String pw = new String(HexBin.encode(MessageDigest.getInstance("MD5").digest(new String(pwd.getPassword()).getBytes()))).toLowerCase();
					String result = Helper.getURLContent(new URL("http://dakror.de/mp-api/login?username=" + usr.getText() + "&password=" + pw + "&ip=" + Game.ip.getHostAddress()));
					if (result.equals("faillogin")) JOptionPane.showMessageDialog(frame, "Anmeldung fehlgeschlagen!", "Anmeldung fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
					else if (result.startsWith("true"))
					{
						Game.user = new User(new JSONObject(Helper.getURLContent(new URL("http://dakror.de/mp-api/players?id=" + result.substring(result.indexOf(":") + 1)))).getString("USERNAME"), null, 0);
						
						frame.dispose();
						Game.currentFrame.toggleLayer(LoginLayer.this);
					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		login.setPreferredSize(pwd.getPreferredSize());
		login.setBackground(new Color(0, 0, 0, 0));
		p.add(login);
		
		frame.setContentPane(p);
		
		frame.setVisible(true);
	}
}
