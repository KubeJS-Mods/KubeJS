package dev.latvian.kubejs.documentation;

/**
 * @author LatvianModder
 */
public class DocumentedBinding implements Comparable<DocumentedBinding>
{
	public final String name;
	public final Class type;
	public final String value;

	public DocumentedBinding(String n, Class t, String v)
	{
		name = n;
		type = t;
		value = v;
	}

	@Override
	public int compareTo(DocumentedBinding o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}