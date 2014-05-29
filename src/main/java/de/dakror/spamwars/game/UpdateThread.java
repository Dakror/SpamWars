package de.dakror.spamwars.game;

import java.io.IOException;

import de.dakror.gamesetup.Updater;
import de.dakror.spamwars.net.packet.Packet15Alive;

/**
 * @author Dakror
 */
public class UpdateThread extends Updater
{
	public UpdateThread()
	{
		super();
	}
	
	@Override
	public void update()
	{
		if (Game.world != null) Game.world.update(tick);
		if (tick % 60 == 0)
		{
			try
			{
				Game.client.sendPacketToCentral(new Packet15Alive(false));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
