package de.dakror.spamwars.game;

import de.dakror.gamesetup.Updater;
import de.dakror.spamwars.net.packet.Packet03Attribute;

/**
 * @author Dakror
 */
public class ServerUpdater extends Thread
{
	public int tick, ticks;
	public long time, time2;
	
	public int countdown = -1;
	
	public int speed = 1;
	
	public boolean closeRequested = false;
	
	public ServerUpdater()
	{
		setName("ServerUpdater-Thread");
		setPriority(Thread.MAX_PRIORITY);
		start();
	}
	
	@Override
	public void run()
	{
		tick = 0;
		time = System.currentTimeMillis();
		while (!closeRequested)
		{
			if (Game.server == null) break;
			if (tick == Integer.MAX_VALUE) tick = 0;
			
			if (Game.server.world != null) Game.server.world.updateServer(tick);
			
			if (countdown > -1 && time2 == 0)
			{
				time2 = System.currentTimeMillis();
				try
				{
					Game.server.sendPacketToAllClients(new Packet03Attribute("countdown", countdown, false));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if (System.currentTimeMillis() - time2 >= 1000 && time2 > 0)
			{
				if (countdown <= 0)
				{
					time2 = 0;
					countdown = -1;
				}
				else
				{
					countdown--;
					try
					{
						Game.server.sendPacketToAllClients(new Packet03Attribute("countdown", countdown, false));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					time2 = System.currentTimeMillis();
				}
			}
			
			try
			{
				tick++;
				ticks++;
				Thread.sleep(Math.round(Updater.TIMEOUT / (float) speed));
			}
			catch (InterruptedException e)
			{}
		}
	}
}
