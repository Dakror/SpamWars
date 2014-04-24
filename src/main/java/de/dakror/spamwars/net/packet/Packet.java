package de.dakror.spamwars.net.packet;

import java.util.Arrays;

/**
 * @author Dakror
 */
public abstract class Packet
{
	public static enum PacketTypes
	{
		INVALID,
		CONNECT,
		DISCONNECT,
		REJECT,
		ATTRIBUTE,
		PLAYERLIST,
		CHUNK,
		PLAYER,
		ANIMATION,
		PROJECTILE,
		KILL,
		ENTITYSTATUS,
		GAMEINFO,
		STOMP,
		
		HOSTGAME,
		LOGIN,
		ALIVE,
		GAMES,
		JOINGAME,
		ENDGAME,
		
		;
		public int getID()
		{
			return ordinal() - 1;
		}
	}
	
	public byte packetID;
	public boolean forServer;
	
	public Packet(int packetID)
	{
		this.packetID = (byte) packetID;
		forServer = false;
	}
	
	public Packet(int packetID, boolean forServer)
	{
		this.packetID = (byte) packetID;
		this.forServer = forServer;
	}
	
	public void load(byte[] data)
	{
		forServer = isForServer(data);
	}
	
	protected abstract byte[] getPacketData();
	
	public byte[] getData()
	{
		byte[] strData = getPacketData();
		
		byte[] data = new byte[strData.length + 2];
		data[0] = packetID;
		data[1] = (byte) (forServer ? 127 : -128);
		
		System.arraycopy(strData, 0, data, 2, strData.length);
		
		return data;
	}
	
	public static String readData(byte[] data)
	{
		return new String(Arrays.copyOfRange(data, 2, data.length)).trim();
	}
	
	public PacketTypes getType()
	{
		return Packet.lookupPacket(packetID);
	}
	
	public static boolean isForServer(byte[] data)
	{
		return data[1] == (byte) 127;
	}
	
	public static PacketTypes lookupPacket(int id)
	{
		for (PacketTypes pt : PacketTypes.values())
		{
			if (pt.getID() == id) return pt;
		}
		
		return PacketTypes.INVALID;
	}
}
