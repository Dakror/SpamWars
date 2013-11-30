package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.layer.MPLayer;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet0Connect;
import de.dakror.spamwars.net.packet.Packet2Attribute;
import de.dakror.spamwars.net.packet.Packet3ServerInfo;
import de.dakror.spamwars.net.packet.Packet4World;
import de.dakror.spamwars.net.packet.Packet5PlayerData;
import de.dakror.spamwars.net.packet.Packet6Animation;
import de.dakror.spamwars.net.packet.Packet7Projectile;
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
	
	Packet3ServerInfo serverInfo;
	
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
				Packet0Connect p = new Packet0Connect(data);
				if (p.getUsername().equals(Game.user.getUsername())) connected = true;
				
				packet = p;
				break;
			}
			case SERVERINFO:
			{
				Packet3ServerInfo p = new Packet3ServerInfo(data);
				
				for (User u : p.getUsers())
					if (u.getUsername().equals(Game.user.getUsername()) && Game.user.getIP() == null) Game.user = u;
				
				serverInfo = p;
				packet = p;
				break;
			}
			case WORLD:
			{
				Packet4World p = new Packet4World(data);
				Game.world = p.getWorld();
				Game.world.addEntity(Game.player, false);
				
				for (User u : serverInfo.getUsers())
				{
					if (!u.getUsername().equals(Game.user.getUsername())) Game.world.addEntity(new Player(0, 0, u), false);
				}
				
				Game.currentFrame.removeLayer(Game.currentFrame.getActiveLayer());
				
				packet = p;
				break;
			}
			case ATTRIBUTE:
			{
				Packet2Attribute p = new Packet2Attribute(data);
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
				Packet5PlayerData p = new Packet5PlayerData(data);
				for (Entity e : Game.world.entities)
				{
					if (e instanceof Player && !((Player) e).getUser().getUsername().equals(Game.user.getUsername()))
					{
						e.setPos(p.getPosition());
						// e.setVelocity(p.getVelocity());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						((Player) e).setStyle(p.getStyle());
						((Player) e).getWeapon().rot2 = p.getRot();
						e.update = false;
						break;
					}
				}
				break;
			}
			case ANIMATION:
			{
				Packet6Animation p = new Packet6Animation(data);
				Game.world.addAnimation(p.getAnimation(), false);
				
				break;
			}
			case PROJECTILE:
			{
				Packet7Projectile p = new Packet7Projectile(data);
				Game.world.addProjectile(p.getProjectile(), false);
				
				break;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
		}
		
		if (Game.currentGame.getActiveLayer() instanceof MPLayer && packet != null) ((MPLayer) Game.currentGame.getActiveLayer()).onPacketReceived(packet);
	}
	
	public void sendPacket(Packet p) throws IOException
	{
		if (serverIP == null)
		{
			System.err.println("Connect to a server first!");
			return;
		}
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
			sendPacket(new Packet0Connect(Game.user.getUsername(), CFG.VERSION));
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
}
