package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JOptionPane;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.layer.LobbyLayer;
import de.dakror.spamwars.layer.MPLayer;
import de.dakror.spamwars.layer.MenuLayer;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet00Connect;
import de.dakror.spamwars.net.packet.Packet01Disconnect;
import de.dakror.spamwars.net.packet.Packet01Disconnect.Cause;
import de.dakror.spamwars.net.packet.Packet02Reject;
import de.dakror.spamwars.net.packet.Packet03Attribute;
import de.dakror.spamwars.net.packet.Packet04ServerInfo;
import de.dakror.spamwars.net.packet.Packet05World;
import de.dakror.spamwars.net.packet.Packet06PlayerData;
import de.dakror.spamwars.net.packet.Packet07Animation;
import de.dakror.spamwars.net.packet.Packet08Projectile;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public class Client extends Thread
{
	DatagramSocket socket;
	public boolean running;
	
	boolean connected;
	
	InetAddress serverIP;
	
	public Packet04ServerInfo serverInfo;
	
	public Client()
	{
		try
		{
			socket = new DatagramSocket();
			setName("Client-Thread");
			setPriority(MAX_PRIORITY);
			connected = false;
			start();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void parsePacket(byte[] data)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		
		Packet packet = null;
		
		switch (type)
		{
			case INVALID:
			{
				CFG.p("received invalid packet: " + new String(data));
				return;
			}
			case CONNECT:
			{
				Packet00Connect p = new Packet00Connect(data);
				if (p.getUsername().equals(Game.user.getUsername())) connected = true;
				else if (Game.world != null) Game.world.addEntity(new Player(0, 0, new User(p.getUsername(), null, 0)));
				
				packet = p;
				break;
			}
			case DISCONNECT:
			{
				Packet01Disconnect p = new Packet01Disconnect(data);
				if (p.getUsername().equals("##"))
				{
					if (!serverIP.equals(Game.user.getIP())) JOptionPane.showMessageDialog(Game.w, p.getCause().getDescription(), "Spiel beendet", JOptionPane.ERROR_MESSAGE);
					connected = false;
					serverInfo = null;
					serverIP = null;
					
					Game.currentGame.setLayer(new MenuLayer());
				}
				else if (p.getUsername().equals(Game.user.getUsername()))
				{
					connected = false;
					serverInfo = null;
					serverIP = null;
				}
				else if (Game.world != null)
				{
					for (Entity e : Game.world.entities)
					{
						if (e instanceof Player)
						{
							if (((Player) e).getUser().getUsername().equals(p.getUsername()))
							{
								Game.world.entities.remove(e);
								break;
							}
						}
					}
					try
					{
						sendPacket(new Packet04ServerInfo());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
				packet = p;
				break;
			}
			case REJECT:
			{
				connected = false;
				serverInfo = null;
				serverIP = null;
				packet = new Packet02Reject(data);
				
				break;
			}
			case SERVERINFO:
			{
				Packet04ServerInfo p = new Packet04ServerInfo(data);
				
				for (User u : p.getUsers())
					if (u.getUsername().equals(Game.user.getUsername()) && Game.user.getIP() == null) Game.user = u;
				
				serverInfo = p;
				packet = p;
				break;
			}
			case WORLD:
			{
				Packet05World p = new Packet05World(data);
				
				if (!(Game.currentGame.getActiveLayer() instanceof LobbyLayer)) Game.currentGame.addLayer(new LobbyLayer());
				
				Game.world = p.getWorld();
				Game.world.addEntity(Game.player);
				
				for (User u : serverInfo.getUsers())
					if (!u.getUsername().equals(Game.user.getUsername())) Game.world.addEntity(new Player(0, 0, u));
				
				packet = p;
				break;
			}
			case ATTRIBUTE:
			{
				Packet03Attribute p = new Packet03Attribute(data);
				if (p.getKey().equals("pos"))
				{
					int x = Integer.parseInt(p.getValue().substring(0, p.getValue().indexOf(",")));
					int y = Integer.parseInt(p.getValue().substring(p.getValue().indexOf(",") + 1));
					
					if (Game.player == null)
					{
						Game.player = new Player(x, y, Game.user);
					}
					else
					{
						Game.player.x = x;
						Game.player.y = y;
					}
				}
				packet = p;
				break;
			}
			case PLAYER:
			{
				Packet06PlayerData p = new Packet06PlayerData(data);
				for (Entity e : Game.world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getUsername().equals(p.getUsername()) && !((Player) e).getUser().getUsername().equals(Game.user.getUsername()))
					{
						e.setPos(p.getPosition());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						e.setLife(p.getLife());
						((Player) e).setStyle(p.getStyle());
						if (((Player) e).getWeapon().type != p.getWeaponType()) ((Player) e).setWeapon(p.getWeaponType());
						((Player) e).getWeapon().rot2 = p.getRot();
						e.update = false;
						break;
					}
				}
				break;
			}
			case ANIMATION:
			{
				Packet07Animation p = new Packet07Animation(data);
				Game.world.addAnimation(p.getAnimation(), false);
				
				break;
			}
			case PROJECTILE:
			{
				Packet08Projectile p = new Packet08Projectile(data);
				Game.world.addProjectile(p.getProjectile(), false);
				
				break;
			}
			case KILL:
			{
				packet = new Packet09Kill(data);
				
				break;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
		}
		
		if (Game.currentGame.getActiveLayer() instanceof MPLayer && packet != null) ((MPLayer) Game.currentGame.getActiveLayer()).onPacketReceived(packet);
	}
	
	public void sendPacket(Packet p) throws IOException
	{
		if (serverIP == null) return;
		
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, Server.PORT);
		socket.send(packet);
	}
	
	public void connectToServer(InetAddress ip)
	{
		if (connected)
		{
			System.err.println("Client is already connected to a server. Disconnect first!");
			return;
		}
		
		serverIP = ip;
		try
		{
			sendPacket(new Packet00Connect(Game.user.getUsername(), CFG.VERSION));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void disconnect()
	{
		if (!connected) return;
		try
		{
			sendPacket(new Packet01Disconnect(Game.user.getUsername(), Cause.USER_DISCONNECT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
