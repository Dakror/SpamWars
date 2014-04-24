package de.dakror.spamwars.net.packet;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.spamwars.game.anim.Animation;


/**
 * @author Dakror
 */
public class Packet07Animation extends Packet
{
	Animation animation;
	
	public Packet07Animation(Animation anim, boolean forServer)
	{
		super(7, forServer);
		animation = anim;
	}
	
	public Packet07Animation(byte[] data)
	{
		super(7);
		load(data);
		
		try
		{
			animation = new Animation(new JSONObject(readData(data)));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public Animation getAnimation()
	{
		return animation;
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return animation.serialize().toString().getBytes();
	}
}
