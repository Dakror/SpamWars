package de.dakror.spamwars.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.gamesetup.ui.Component;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.weapon.WeaponType;

/**
 * @author Dakror
 */
public class KillLabel extends Component {
	public static final int TIME = 60 * 10; // 10 secs
	public static final int SPACE = 42;
	int startTick;
	
	public boolean dead;
	
	String killer, killed;
	WeaponType type;
	BufferedImage weapon;
	
	public KillLabel(int y, Player killer, Player killed, WeaponType type) {
		super(Game.getWidth() - 380, y, 0, 40);
		
		dead = false;
		this.killer = killer.getUser().getUsername();
		this.killed = killed.getUser().getUsername();
		this.type = type;
		
		if (type == WeaponType.WEAPON) {
			weapon = killer.getWeapon().getImage();
			Dimension dim = Helper.scaleTo(new Dimension(weapon.getWidth(), weapon.getHeight()), new Dimension(150, 20));
			if (dim.width < weapon.getWidth() || dim.height < weapon.getHeight()) weapon = Helper.toBufferedImage(weapon.getScaledInstance(dim.width, dim.height, BufferedImage.SCALE_SMOOTH));
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (dead) return;
		Font f = g.getFont();
		g.setFont(new Font("", Font.PLAIN, 18));
		
		if (width == 0) {
			FontMetrics fm = g.getFontMetrics();
			
			if (WeaponType.getMessage(type) == null) width = fm.stringWidth(killer) + fm.stringWidth(killed) + 150;
			else width = fm.stringWidth(WeaponType.getMessage(type).replace("%killer%", killer).replace("%dead%", killed)) + 30;
			
			x = Game.getWidth() - width - 30;
		}
		
		Color o = g.getColor();
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g.setColor(Color.black);
		g.fillRect(x + 8, y + 8, width - 16, height - 16);
		g.setComposite(c);
		Helper.drawOutline(x, y, width, height, false, g);
		
		g.setColor(killer.equals(Game.user.getUsername()) ? Color.decode("#3333ff") : Color.white);
		
		if (type == WeaponType.WEAPON) {
			if (killed.equals(killer)) {
				int[] ints = Helper.drawHorizontallyCenteredString(killer, x - 30, width, y + 26, g, 18);
				g.drawImage(Game.getImage("icon/kill.png"), ints[0] + ints[1] + 25, y + 10, Game.w);
			} else {
				Helper.drawString(killer, x + 15, y + 26, g, 18);
				g.setColor(killed.equals(Game.user.getUsername()) ? Color.decode("#3333ff") : Color.white);
				Helper.drawRightAlignedString(killed, x + width - 15, y + 26, g, 18);
				
				if (weapon != null) g.drawImage(weapon, x + (width - weapon.getWidth()) / 2, y + 10, Game.w);
			}
		} else {
			Helper.drawString(WeaponType.getMessage(type).replace("%killer%", killer).replace("%dead%", killed), x + 15, y + 26, g, 18);
		}
		g.setColor(o);
		g.setFont(f);
	}
	
	@Override
	public void update(int tick) {
		if (dead) return;
		if (startTick == 0) {
			startTick = tick;
			return;
		}
		
		if (tick - startTick >= TIME) {
			if (y > SPACE) y -= SPACE;
			else dead = true;
		}
	}
}
