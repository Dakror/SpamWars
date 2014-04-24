package de.dakror.spamwars.net.central;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import de.dakror.spamwars.net.Server;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet.PacketTypes;

/**
 * @author Dakror
 */
public class CentralServer
{
	public static final int PORT = 19950;
	static DatagramSocket socket;
	static ArrayList<Game> games;
	
	public static void main(String[] args)
	{
		games = new ArrayList<>();
		try
		{
			socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		while (true)
		{
			byte[] data = new byte[Server.PACKETSIZE];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try
			{
				socket.receive(packet);
				parsePacket(data, packet.getAddress(), packet.getPort());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void parsePacket(byte[] data, InetAddress address, int port)
	{
		PacketTypes type = Packet.lookupPacket(data[0]);
		switch (type)
		{
			default:
				break;
		}
	}
}
