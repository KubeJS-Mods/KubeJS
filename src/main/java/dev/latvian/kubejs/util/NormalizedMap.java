package dev.latvian.kubejs.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class NormalizedMap extends LinkedHashMap<String, Object> implements Normalized, Copyable, NormalizedObjectChangeListener
{
	public NormalizedObjectChangeListener changeListener;

	NormalizedMap()
	{
	}

	@Override
	public String toString()
	{
		if (isEmpty())
		{
			return "{}";
		}

		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(StringBuilder builder)
	{
		if (isEmpty())
		{
			builder.append("{}");
			return;
		}

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
				Object o = entry.getValue();

				if (o instanceof Normalized)
				{
					((Normalized) o).toString(builder);
				}
				else
				{
					builder.append(o);
				}
			}
		}

		builder.append('}');
	}

	@Override
	public NormalizedMap copy()
	{
		NormalizedMap map = new NormalizedMap();

		for (Map.Entry<String, Object> entry : entrySet())
		{
			map.put(entry.getKey(), UtilsJS.copy(entry.getValue()));
		}

		return map;
	}

	@Override
	public void onChanged()
	{
		if (changeListener != null)
		{
			changeListener.onChanged();
		}
	}

	@Override
	public Object put(String key, Object value)
	{
		if (value instanceof NormalizedMap)
		{
			((NormalizedMap) value).changeListener = this;
		}
		else if (value instanceof NormalizedList)
		{
			((NormalizedList) value).changeListener = this;
		}

		return super.put(key, value);
	}
}