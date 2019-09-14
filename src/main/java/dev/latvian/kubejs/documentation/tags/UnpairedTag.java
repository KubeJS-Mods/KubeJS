package dev.latvian.kubejs.documentation.tags;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class UnpairedTag extends Tag
{
	public UnpairedTag(String n)
	{
		super(n);
	}

	@Override
	public void build(StringBuilder builder)
	{
		builder.append('<');
		builder.append(name);

		if (attributes != null && !attributes.isEmpty())
		{
			for (Map.Entry<String, String> map : attributes.entrySet())
			{
				builder.append(' ');
				builder.append(map.getKey());
				builder.append('=');
				builder.append('"');
				builder.append(map.getValue());
				builder.append('"');
			}
		}

		builder.append('>');
	}
}