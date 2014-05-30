package de.dakror.spamwars.net;

import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet04PlayerList;
import de.dakror.spamwars.net.packet.Packet11GameInfo;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.util.ClientBase;

/**
 * @author Dakror
 */
public class Networker extends ClientBase
{
	public Packet04PlayerList playerList;
	public Packet11GameInfo gameInfo;
	
	@Override
	public void onConnectDone(ConnectEvent e)
	{
		CFG.p("Connected to Central Server.");
		Game.currentGame.removeLayer(Game.currentGame.getActiveLayer()); // ConnectingLayer
	}
	
	@Override
	public void onDisconnectDone(ConnectEvent e)
	{
		CFG.p("Disconnected from Central Server.");
		System.exit(0);
	}
	
	public void sendPacket(Packet packet)
	{
		Game.warp.sendUDPUpdatePeers(packet.getData());
	}
}
