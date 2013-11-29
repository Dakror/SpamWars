package de.dakror.spamwars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.settings.CFG;

/**
 * @author Dakror
 */
public abstract class NetHandler extends Thread
{
	protected DatagramSocket socket;
	public boolean running;
	
	public NetHandler()
	{
		try
		{
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	public NetHandler(InetAddress ip)
	{
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(ip, CFG.SERVER_PORT));
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
		
		shutdown();
	}
	
	public abstract void parsePacket(byte[] data);
	
	public abstract void sendPacket(Packet p) throws IOException;
	
	public abstract void shutdown();
}
