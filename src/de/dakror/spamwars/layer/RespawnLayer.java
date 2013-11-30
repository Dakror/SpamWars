package de.dakror.spamwars.layer;

import java.awt.Graphics2D;

import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public class RespawnLayer extends MPLayer
{
	public RespawnLayer()
	{
		modal = true;
	}
	
	@Override
	public void draw(Graphics2D g)
	{}
	
	@Override
	public void update(int tick)
	{}
	
	@Override
	public void onPacketReceived(Packet p)
	{}
	
	@Override
	public void init()
	{}
}
