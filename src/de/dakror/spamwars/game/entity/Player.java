package de.dakror.spamwars.game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.anim.Animation;
import de.dakror.spamwars.game.projectile.Projectile;
import de.dakror.spamwars.game.weapon.Action;
import de.dakror.spamwars.game.weapon.Weapon;
import de.dakror.spamwars.game.weapon.Weapon.FireMode;
import de.dakror.spamwars.game.weapon.WeaponType;
import de.dakror.spamwars.game.world.Tile;
import de.dakror.spamwars.layer.RespawnLayer;
import de.dakror.spamwars.net.User;
import de.dakror.spamwars.net.packet.Packet06PlayerData;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.net.packet.Packet12Stomp;


/**
 * @author Dakror
 */
public class Player extends Entity
{
	public boolean left, right, up, down;
	
	public boolean lookingLeft = false;
	
	private int style = 0;
	
	private Weapon weapon;
	
	/**
	 * 0 stand, 0-10 = walking, 11 = jump
	 */
	public int frame = 0;
	
	Point hand = new Point(0, 0);
	
	Point mouse = new Point(0, 0);
	
	User user;
	
	public Player(float x, float y, User user)
	{
		super(x, y, 72, 97);
		
		style = (int) (Math.random() * 3 + 1);
		bump = new Rectangle(10, 7, 44, 84);
		gravity = true;
		
		this.user = user;
		
		life = maxlife = 100;
		
		setWeapon(WeaponType.HANDGUN);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		float mx = x + Game.world.x;
		float my = y + Game.world.y;
		
		Color o = g.getColor();
		g.setColor(Color.darkGray);
		Helper.drawHorizontallyCenteredString(user.getUsername(), (int) mx, width, (int) my - 5, g, 20);
		
		if (!user.getUsername().equals(Game.user.getUsername())) Helper.drawProgressBar((int) mx, (int) (my + height - 5), width, life / (float) maxlife, "ff3232", g);
		g.setColor(o);
		
		AffineTransform old = g.getTransform();
		if (lookingLeft)
		{
			AffineTransform at = g.getTransform();
			at.translate((mx + width / 2) * 2, 0);
			at.scale(-1, 1);
			g.setTransform(at);
		}
		
		if (frame >= 0 && frame <= 10)
		{
			String frame = (this.frame + 1) + "";
			if (frame.length() == 1) frame = "0" + frame;
			
			g.drawImage(Game.getImage("entity/player/p" + getStyle() + "/p" + getStyle() + "_walk" + frame + ".png"), (int) mx, (int) my, Game.w);
		}
		else if (frame == 11)
		{
			g.drawImage(Game.getImage("entity/player/p" + getStyle() + "/p" + getStyle() + "_jump.png"), (int) mx, (int) my, Game.w);
		}
		g.setTransform(old);
		
		old = g.getTransform();
		AffineTransform at = g.getTransform();
		at.translate(hand.x + mx, hand.y + my);
		g.setTransform(at);
		
		weapon.draw(g);
		
		g.setTransform(old);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0) return;
		
		handleMouse(e, false);
	}
	
	public Vector getWeaponPoint()
	{
		Vector exit = new Vector(weapon.getExit()).mul(Weapon.scale);
		exit.x = 0;
		
		Vector point = getPos().add(new Vector(hand)).sub(new Vector(weapon.getGrab()).mul(Weapon.scale)).add(exit);
		
		return point;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0) return;
		
		handleMouse(e, true);
	}
	
	public void handleMouse(MouseEvent e, boolean target)
	{
		
		lookingLeft = e.getX() < x + width / 2;
		mouse = e.getPoint();
		
		Vector dif = new Vector(e.getPoint()).sub(getWeaponPoint());
		
		weapon.rot2 = (float) Math.toRadians(dif.getAngleOnXAxis() * (lookingLeft ? -1 : 1));
		
		if (target)
		{
			Point p = new Point((int) x + hand.x, (int) y + hand.y);
			Point tile = Game.world.getTile(p.x, p.y);
			Tile t = Tile.values()[Game.world.getTileIdAtPixel(p.x, p.y)];
			
			if (t.getBump() != null)
			{
				Rectangle r = (Rectangle) t.getBump().clone();
				r.translate(tile.x * Tile.SIZE, tile.y * Tile.SIZE);
				if (r.contains(p)) return;
			}
			
			weapon.target(new Vector(e.getPoint()));
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0 || weapon.fireMode != FireMode.AUTO) return;
		
		handleMouse(e, true);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0) return;
		
		weapon.target(null);
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0) return;
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_A:
			{
				left = true;
				break;
			}
			case KeyEvent.VK_D:
			{
				right = true;
				break;
			}
			case KeyEvent.VK_SPACE:
			{
				if (!airborne) getVelocity().y = -15;
				up = true;
				break;
			}
			// case KeyEvent.VK_SHIFT:
			// {
			// down = true;
			// break;
			// }
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (!user.getUsername().equals(Game.user.getUsername()) || life <= 0) return;
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_A:
			{
				left = false;
				break;
			}
			case KeyEvent.VK_D:
			{
				right = false;
				break;
			}
			case KeyEvent.VK_SPACE:
			{
				up = false;
				break;
			}
			// case KeyEvent.VK_S:
			// {
			// down = false;
			// break;
			// }
		}
	}
	
	@Override
	protected void tick(int tick)
	{
		int speed = airborne ? 3 : 4;
		
		if (user.getUsername().equals(Game.user.getUsername()))
		{
			if (left) getVelocity().x = -speed;
			if (right) getVelocity().x = speed;
			if (!airborne && getVelocity().x != 0 && tick % 4 == 0)
			{
				frame = frame < 0 ? 0 : frame;
				
				frame = (frame + 1) % 6;
			}
			else if (airborne)
			{
				frame = 11;
			}
			
			if (!left && !right)
			{
				frame = 3;
				getVelocity().x = 0;
			}
			
			int mx = (Game.getWidth() - width) / 2;
			int my = (Game.getHeight() - height) / 2;
			
			if (life > 0)
			{
				Game.world.x = mx - x;
				Game.world.y = my - y;
				
				float fx = Game.world.x + Game.world.width;
				
				if (fx < Game.getWidth() && fx > 0) Game.world.x += Game.getWidth() - fx;
				else if (Game.world.x > 0) Game.world.x = 0;
			}
			else
			{
				left = right = up = down = false;
				weapon.target(null);
			}
		}
		
		weapon.left = lookingLeft;
		weapon.update(tick);
		if (lookingLeft) hand = new Point(0, 60);
		else hand = new Point(65, 60);
		
		try
		{
			if (user.getUsername().equals(Game.user.getUsername()) && tick % 2 == 0) Game.client.sendPacket(new Packet06PlayerData(this));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getStyle()
	{
		return style;
	}
	
	public void setStyle(int style2)
	{
		style = style2;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public Weapon getWeapon()
	{
		return weapon;
	}
	
	public void setWeapon(WeaponType weapon)
	{
		try
		{
			this.weapon = (Weapon) weapon.getClass1().getConstructor().newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void revive()
	{
		gravity = true;
		Vector spawn = Game.world.getBestSpawnPoint();
		x = spawn.x * Tile.SIZE + (int) (Math.random() * Tile.SIZE - Tile.SIZE / 2);
		y = spawn.y * Tile.SIZE - height + Tile.SIZE + (int) (Math.random() * Tile.SIZE - Tile.SIZE / 2);
		life = maxlife;
		weapon.ammo = weapon.magazine;
		weapon.capacity = weapon.capacityMax;
	}
	
	public void dealDamage(float damage, Object source)
	{
		life -= damage;
		if (life <= 0 && x > -10000000)
		{
			if (source instanceof Projectile)
			{
				WeaponType w = null;
				for (Entity e : Game.world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getUsername().equals(((Projectile) source).getUsername())) w = ((Player) e).getWeapon().type;
				}
				
				try
				{
					Game.client.sendPacket(new Packet09Kill(((Projectile) source).getUsername(), Game.user.getUsername(), w));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else if (source instanceof Action)
			{
				try
				{
					Game.client.sendPacket(new Packet09Kill(((Action) source).username, Game.user.getUsername(), ((Action) source).type));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			Game.world.addAnimation(new Animation("expl/11", getPos().clone().sub(new Vector((192 - width) / 2, (192 - height) / 2)), 2, 192, 24), true);
			x = -10000000;
			gravity = false;
			life = 0;
			Game.currentGame.addLayer(new RespawnLayer());
		}
	}
	
	@Override
	public void updateServer(int tick)
	{
		for (Entity e : Game.server.world.entities)
		{
			if (e.isEnabled() && e.getBump(0, 0).intersects(getBump(0, 0)))
			{
				if (weapon.canRefill() && e instanceof AmmoBox)
				{
					e.setEnabled(false, true, true);
					weapon.refill(AmmoBox.AMMO);
					try
					{
						Game.server.sendPacketToAllClients(new Packet06PlayerData(this));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
				if (life < maxlife && e instanceof HealthBox)
				{
					e.setEnabled(false, true, true);
					life = life + HealthBox.HEALTH > maxlife ? maxlife : life + HealthBox.HEALTH;
					try
					{
						Game.server.sendPacketToAllClients(new Packet06PlayerData(this));
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	public void stop()
	{
		left = false;
		right = false;
		up = false;
		down = false;
	}
	
	@Override
	protected void onHitGround(Vector velocity)
	{
		float maxFall = 30;
		float dmgFactor = 5;
		if (velocity.y > maxFall) dealDamage((velocity.y - maxFall) * dmgFactor, new Action(WeaponType.FALL_DAMAGE, Game.user.getUsername()));
	}
	
	@Override
	protected void onHitEntity(Entity e, Vector velocity)
	{
		if (e instanceof Player && velocity.y > 14)
		{
			try
			{
				Game.client.sendPacket(new Packet12Stomp(Game.user.getUsername(), ((Player) e).getUser().getUsername(), velocity.y * 2));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
