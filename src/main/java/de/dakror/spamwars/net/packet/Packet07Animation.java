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
	
	public Packet07Animation(Animation anim)
	{
		super(7);
		animation = anim;
	}
	
	public Packet07Animation(byte[] data)
	{
		super(7);
		
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
