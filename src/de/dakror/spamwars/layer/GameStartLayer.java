package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.gamesetup.util.Helper;
import de.dakror.spamwars.game.Game;
import de.dakror.spamwars.net.packet.Packet;
import de.dakror.spamwars.net.packet.Packet03Attribute;

/**
 * @author Dakror
 */
public class GameStartLayer extends MPLayer
{
	int cd = 5;
	
	public GameStartLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		drawModality(g);
		Helper.drawHorizontallyCenteredString("Spiel startet in", Game.getWidth(), Game.getHeight() / 3, g, 70);
		
		int cd = this.cd;
		if (cd < 0) cd = 0;
		
		Helper.drawHorizontallyCenteredString(cd + "", Game.getWidth(), Game.getHeight() / 2, g, 150);
	}
	
	@Override
	public void update(int tick)
	{
		if (cd == 0 && Game.server != null)
		{
			Game.server.startGame();
			cd = -1;
		}
	}
	
	@Override
	public void onPacketReceived(Packet p)
	{
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("countdown")) cd = Integer.parseInt(((Packet03Attribute) p).getValue());
		if (p instanceof Packet03Attribute && ((Packet03Attribute) p).getKey().equals("worldsize")) Game.currentGame.fadeTo(1, 0.05f);
	}
	
	@Override
	public void init()
	{
		if (Game.server != null) Game.server.updater.countdown = 5;
	}
}
