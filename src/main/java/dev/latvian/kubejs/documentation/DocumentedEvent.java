package dev.latvian.kubejs.documentation;

import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
public class DocumentedEvent
{
	public final String eventID;
	public final Class eventClass;
	public String doubleParam = "";
	public Side sideOnly = null;
	public boolean canCancel = false;

	public DocumentedEvent(String s, Class c)
	{
		eventID = s;
		eventClass = c;
	}

	public DocumentedEvent doubleParam(String s)
	{
		doubleParam = s;
		return this;
	}

	public DocumentedEvent canCancel()
	{
		canCancel = true;
		return this;
	}

	public DocumentedEvent sideOnly(Side s)
	{
		sideOnly = s;
		return this;
	}

	public DocumentedEvent clientOnly()
	{
		return sideOnly(Side.CLIENT);
	}

	public DocumentedEvent serverOnly()
	{
		return sideOnly(Side.SERVER);
	}
}