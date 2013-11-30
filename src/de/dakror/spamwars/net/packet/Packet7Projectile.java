package de.dakror.spamwars.net.packet;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.game.projectile.Projectile;

/**
 * @author Dakror
 */
public class Packet7Projectile extends Packet
{
	Projectile p;
	
	public Packet7Projectile(Projectile p)
	{
		super(7);
		this.p = p;
	}
	
	public Packet7Projectile(byte[] data)
	{
		super(7);
		
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
