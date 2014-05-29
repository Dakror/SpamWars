package de.dakror.spamwars.net.packet;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.game.projectile.Projectile;

/**
 * @author Dakror
 */
public class Packet08Projectile extends Packet
{
	Projectile p;
	
	public Packet08Projectile(Projectile p, boolean forServer)
	{
		super(8, forServer);
		this.p = p;
	}
	
	public Packet08Projectile(byte[] data)
	{
		super(8);
		load(data);
		
		try
		{
			JSONObject o = new JSONObject(readData(data));
			p = new Projectile(o);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public Projectile getProjectile()
	{
		return p;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return p.serialize().toString().getBytes();
	}
}
