package dev.latvian.kubejs.documentation;

import net.minecraftforge.api.distmarker.Dist;

/**
 * @author LatvianModder
 */
public class DocumentedEvent implements Comparable<DocumentedEvent>
{
	public final String eventID;
	public final Class eventClass;
	public String doubleParam = "";
	public Dist sideOnly = null;
	public boolean canCancel = false;
	public boolean startup = false;

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

	public DocumentedEvent sideOnly(Dist s)
	{
		sideOnly = s;
		return this;
	}

	public DocumentedEvent clientOnly()
	{
		return sideOnly(Dist.CLIENT);
	}

	public DocumentedEvent serverOnly()
	{
		return sideOnly(Dist.DEDICATED_SERVER);
	}

	public DocumentedEvent startup()
	{
		startup = true;
		return this;
	}

	@Override
	public int compareTo(DocumentedEvent o)
	{
		return eventID.compareToIgnoreCase(o.eventID);
	}
}