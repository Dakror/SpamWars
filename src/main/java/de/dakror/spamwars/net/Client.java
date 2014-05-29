package de.dakror.spamwars.net;

import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;

import de.dakror.spamwars.layer.MenuLayer;
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
		MenuLayer.waiting = false;
	}
}
