package de.dakror.spamwars.net;

import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;

import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.settings.CFG;
import de.dakror.spamwars.util.ClientBase;

/**
 * @author Dakror
 */
public class Client extends ClientBase
{
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
}
