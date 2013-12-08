package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.gamesetup.layer.Alert;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.game.entity.Entity;
import de.dakror.spamwars.game.entity.Player;
import de.dakror.spamwars.game.weapon.Action;
import de.dakror.spamwars.game.weapon.WeaponType;
import de.dakror.spamwars.game.world.World;
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
import de.dakror.spamwars.net.packet.Packet04PlayerList;
import de.dakror.spamwars.net.packet.Packet05Chunk;
import de.dakror.spamwars.net.packet.Packet06PlayerData;
import de.dakror.spamwars.net.packet.Packet07Animation;
import de.dakror.spamwars.net.packet.Packet08Projectile;
import de.dakror.spamwars.net.packet.Packet09Kill;
import de.dakror.spamwars.net.packet.Packet10EntityStatus;
import de.dakror.spamwars.net.packet.Packet11GameInfo;
import de.dakror.spamwars.net.packet.Packet11GameInfo.GameMode;
import de.dakror.spamwars.net.packet.Packet12Stomp;
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
	
	public Packet04PlayerList playerList;
	public Packet11GameInfo gameInfo;
	public long gameStarted;
	
	ArrayList<Packet05Chunk> chunkPackets = new ArrayList<>();
	
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
				if (p.getUsername().equals("##") && serverIP != null)
				{
					setDisconnected();
					Game.currentGame.addLayer(new Alert(p.getCause().getDescription(), null));
				}
				else if (p.getUsername().equals(Game.user.getUsername()))
				{
					setDisconnected();
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
						sendPacket(new Packet04PlayerList());
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
				playerList = null;
				serverIP = null;
				packet = new Packet02Reject(data);
				
				break;
			}
			case PLAYERLIST:
			{
				Packet04PlayerList p = new Packet04PlayerList(data);
				
				for (User u : p.getUsers())
				{
					if (u.getUsername().equals(Game.user.getUsername()))
					{
						Game.user = u;
						break;
					}
				}
				
				playerList = p;
				packet = p;
				break;
			}
			case CHUNK:
			{
				Packet05Chunk p = new Packet05Chunk(data);
				
				chunkPackets.add(p);
				
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
				if (p.getKey().equals("user"))
				{
					try
					{
						Game.user = new User(new JSONObject(p.getValue()));
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
				if (p.getKey().equals("worldsize"))
				{
					if (!(Game.currentGame.getActiveLayer() instanceof LobbyLayer)) Game.currentGame.addLayer(new LobbyLayer());
					
					int w = Integer.parseInt(p.getValue().substring(0, p.getValue().indexOf("_")));
					int h = Integer.parseInt(p.getValue().substring(p.getValue().indexOf("_") + 1));
					Game.world = new World(w, h);
					
					for (Packet05Chunk chunk : chunkPackets)
					{
						Game.world.setData(chunk.getChunk().x, chunk.getChunk().y, Packet05Chunk.SIZE, chunk.getWorldData());
					}
					
					chunkPackets.clear();
					
					Game.world.addEntity(Game.player);
					
					for (User u : playerList.getUsers())
						if (!u.getUsername().equals(Game.user.getUsername())) Game.world.addEntity(new Player(0, 0, u));
				}
				
				packet = p;
				break;
			}
			case PLAYER:
			{
				Packet06PlayerData p = new Packet06PlayerData(data);
				if (Game.world == null) break;
				
				for (Entity e : Game.world.entities)
				{
					if (e instanceof Player && ((Player) e).getUser().getUsername().equals(p.getUsername()))
					{
						e.setPos(p.getPosition());
						((Player) e).frame = p.getFrame();
						((Player) e).lookingLeft = p.isLeft();
						e.setLife(p.getLife());
						((Player) e).setStyle(p.getStyle());
						if (((Player) e).getWeapon().type != p.getWeaponType()) ((Player) e).setWeapon(p.getWeaponType());
						((Player) e).getWeapon().rot2 = p.getRot();
						((Player) e).getWeapon().ammo = p.getAmmo();
						((Player) e).getWeapon().capacity = p.getCapacity();
						
						if (!p.getUsername().equals(Game.user.getUsername())) e.update = false;
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
			case ENTITYSTATUS:
			{
				Packet10EntityStatus p = new Packet10EntityStatus(data);
				
				if (Game.world == null) break;
				
				for (Entity e : Game.world.entities)
				{
					if (!(e instanceof Player) && e.getPos().equals(p.getPos()))
					{
						e.setEnabled(p.getState(), false, false);
					}
				}
				
				packet = p;
				break;
			}
			case GAMEINFO:
			{
				Packet11GameInfo p = new Packet11GameInfo(data);
				
				gameStarted = System.currentTimeMillis();
				gameInfo = p;
				
				packet = p;
				break;
			}
			case STOMP:
			{
				Packet12Stomp p = new Packet12Stomp(data);
				
				Game.player.dealDamage(p.getDamage(), new Action(WeaponType.STOMP, p.getUsername()));
				
				packet = p;
				break;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
		}
		
		if (Game.currentGame.getActiveLayer() instanceof MPLayer && packet != null) ((MPLayer) Game.currentGame.getActiveLayer()).onPacketReceived(packet);
	}
	
	private void setDisconnected()
	{
		connected = false;
		gameStarted = 0;
		playerList = null;
		serverIP = null;
		Game.player = null;
		
		if (Game.server != null) Game.server.shutdown();
		Game.server = null;
		
		Game.world = null;
		Game.currentGame.setLayer(new MenuLayer());
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
			CFG.p("Client is already connected to a server. Disconnect first!");
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
	
	public boolean isGameOver()
	{
		if (gameInfo.getGameMode() == GameMode.ONE_IN_THE_CHAMBER)
		{
			int notOuts = 0;
			for (User u : playerList.getUsers())
			{
				if (u.D < 3) notOuts++;
			}
			
			if (notOuts < 2) return true;
		}
		
		return System.currentTimeMillis() - Game.client.gameStarted >= Game.client.gameInfo.getMinutes() * 60000;
	}
}
