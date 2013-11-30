package de.dakror.spamwars.game.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.Server;

/**
 * @author Dakror
 */
public class LobbyLayer extends Layer
{
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Game.getImage("gui/menu.png"), 0, 0, Game.getWidth(), Game.getHeight(), Game.w);
		Helper.drawImageCenteredRelativeScaled(Game.getImage("gui/startGame.png"), 80, 1920, 1080, Game.getWidth(), Game.getHeight(), g);
	}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void init()
	{
		Game.server = new Server(Game.ip);
		
		Game.client.connectToServer(Game.ip);
	}
}
