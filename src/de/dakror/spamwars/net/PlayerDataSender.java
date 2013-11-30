package de.dakror.spamwars.net;

import de.dakror.gamesetup.Updater;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet5PlayerData;

/**
 * @author Dakror
 */
public class PlayerDataSender extends Thread
{
	public boolean running;
	
	public PlayerDataSender()
	{
		setName("PlayerDataSender");
		start();
	}
	
	@Override
	public void run()
	{
		running = true;
		while (running)
		{
			try
			{
				Game.client.sendPacket(new Packet5PlayerData(Game.player));
				Thread.sleep(Updater.TIMEOUT * 2);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
