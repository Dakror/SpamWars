package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet2Attribute extends Packet
{
	String key;
	String value;
	
	/**
	 * Key format: class_field_type
	 */
	public Packet2Attribute(String key, Object value)
	{
		super(2);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet2Attribute(byte[] data)
	{
		super(2);
		String[] s = readData(data).split(":");
		key = s[0];
		value = s[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (key + ":" + value).getBytes();
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return value;
	}
}
