package de.dakror.spamwars.net.packet;



/**
 * @author Dakror
 */
public class Packet12Stomp extends Packet {
	String username;
	String stomped;
	float damage;
	
	public Packet12Stomp(String username, String stomped, float damage) {
		super(12);
		this.username = username;
		this.stomped = stomped;
		this.damage = damage;
	}
	
	public Packet12Stomp(byte[] data) {
		super(12);
		String[] parts = readData(data).split(":");
		
		username = parts[0];
		stomped = parts[1];
		damage = Float.parseFloat(parts[2]);
	}
	
	@Override
	protected byte[] getPacketData() {
		return (username + ":" + stomped + ":" + damage).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getStomped() {
		return stomped;
	}
	
	public float getDamage() {
		return damage;
	}
	
}
