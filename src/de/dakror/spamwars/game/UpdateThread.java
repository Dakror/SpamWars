package de.dakror.spamwars.game;

import de.dakror.gamesetup.Updater;

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
		Game.world.update(tick);
	}
}
