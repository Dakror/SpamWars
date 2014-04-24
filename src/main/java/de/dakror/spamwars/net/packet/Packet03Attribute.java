package de.dakror.spamwars.net.packet;

/**
 * @author Dakror
 */
public class Packet03Attribute extends Packet
{
	String key;
	String value;
	
	/**
	 * Key format: class_field_type
	 */
	public Packet03Attribute(String key, Object value)
	{
		super(3);
		this.key = key;
		this.value = value.toString();
	}
	
	public Packet03Attribute(byte[] data)
	{
		super(3);
		String[] s = readData(data).split("#~#");
		key = s[0];
		value = s[1];
	}
	
	@Override
	protected byte[] getPacketData()
	{
		return (key + "#~#" + value).getBytes();
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
