package dev.latvian.kubejs.util;

import java.util.ArrayList;

/**
 * @author LatvianModder
 */
public class JsonStyleList extends ArrayList<Object>
{
	@Override
	public String toString()
	{
		if (isEmpty())
		{
			return "[]";
		}

		StringBuilder builder = new StringBuilder();
		builder.append('[');

		for (int i = 0; i < size(); i++)
		{
			if (i > 0)
			{
				builder.append(',');
			}

			builder.append(get(i));
		}

		builder.append(']');
		return builder.toString();
	}
}