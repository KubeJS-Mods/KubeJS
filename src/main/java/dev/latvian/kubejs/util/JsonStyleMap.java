package dev.latvian.kubejs.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class JsonStyleMap extends LinkedHashMap<String, Object>
{
	@Override
	public String toString()
	{
		if (isEmpty())
		{
			return "{}";
		}

		StringBuilder builder = new StringBuilder();
		builder.append('{');
		boolean first = true;

		for (Map.Entry<String, Object> entry : entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				builder.append(',');
			}

			builder.append(entry.getKey());
			builder.append(':');

			if (entry.getValue() instanceof CharSequence)
			{
				builder.append('"');
				builder.append(entry.getValue());
				builder.append('"');
			}
			else
			{
				builder.append(entry.getValue());
			}
		}

		builder.append('}');
		return builder.toString();
	}
}