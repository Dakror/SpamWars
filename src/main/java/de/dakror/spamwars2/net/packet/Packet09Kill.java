package de.dakror.spamwars.net.packet;

import de.dakror.spamwars.game.weapon.WeaponType;

/**
 * @author Dakror
 */
public class Packet09Kill extends Packet
{
	String killer, dead;
	WeaponType weapon;
	
	public Packet09Kill(String killer, String dead, WeaponType weapon, boolean forServer)
	{
		super(9, forServer);
		
		this.killer = killer;
		this.dead = dead;
		this.weapon = weapon;
	}
	
	public Packet09Kill(byte[] data)
	{
		super(9);
		load(data);
		
		String[] parts = readData(data).split(":");
		killer = parts[0];
		dead = parts[1];
		weapon = WeaponType.values()[Integer.parseInt(parts[2])];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (killer + ":" + dead + ":" + weapon.ordinal()).getBytes();
	}
	
	public String getKiller()
	{
		return killer;
	}
	
	public String getDead()
	{
		return dead;
	}
	
	public WeaponType getWeapon()
	{
		return weapon;
	}
}
