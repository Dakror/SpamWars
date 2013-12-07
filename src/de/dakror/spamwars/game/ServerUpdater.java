package de.dakror.spamwars.game;

import de.dakror.gamesetup.Updater;

/**
 * @author Dakror
 */
public class ServerUpdater extends Thread
{
	public int tick, ticks;
	long time;
	
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
			if (tick == Integer.MAX_VALUE) tick = 0;
			
			Game.server.world.updateServer(tick);
			
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
