package de.dakror.spamwars.layer;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.spamwars.net.packet.Packet;

/**
 * @author Dakror
 */
public abstract class MPLayer extends Layer
{
	public abstract void onPacketReceived(Packet p);
}