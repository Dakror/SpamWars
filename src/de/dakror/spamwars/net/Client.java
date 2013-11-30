package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;
import de.dakror.spamwars.net.packet.Packet0Connect;
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
	
	public Client()
	{
		try
		{
			socket = new DatagramSocket();
			setName("Client-Thread");
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
			byte[] data = new byte[CFG.PACKETSIZE];
			
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
		switch (type)
		{
			case INVALID:
			{
				CFG.p("received invalid packet: " + new String(data));
				return;
			}
			default:
				CFG.p("reveived unhandled packet: " + type + " [" + Packet.readData(data) + "]");
		}
	}
	
	public void sendPacket(Packet p) throws IOException
	{
		if (serverIP == null)
		{
			System.err.println("Connect to a server first!");
			return;
		}
		byte[] data = p.getData();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, CFG.SERVER_PORT);
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
}