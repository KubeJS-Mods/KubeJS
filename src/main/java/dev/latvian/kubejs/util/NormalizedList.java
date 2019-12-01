package dev.latvian.kubejs.util;

import java.util.ArrayList;

/**
 * @author LatvianModder
 */
public class NormalizedList extends ArrayList<Object> implements Normalized, Copyable, NormalizedObjectChangeListener
{
	public NormalizedObjectChangeListener changeListener;

	NormalizedList()
	{
	}

	@Override
	public String toString()
	{
		if (isEmpty())
		{
			return "[]";
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
			builder.append("[]");
			return;
		}

		builder.append('[');

		for (int i = 0; i < size(); i++)
		{
			if (i > 0)
			{
				builder.append(',');
			}

			Object o = get(i);

			if (o instanceof Normalized)
			{
				((Normalized) o).toString(builder);
			}
			else
			{
				builder.append(o);
			}
		}

		builder.append(']');
	}

	@Override
	public NormalizedList copy()
	{
		NormalizedList list = new NormalizedList();

		for (Object object : this)
		{
			list.add(UtilsJS.copy(object));
		}

		return list;
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
	public boolean add(Object value)
	{
		if (value instanceof NormalizedMap)
		{
			((NormalizedMap) value).changeListener = this;
		}
		else if (value instanceof NormalizedList)
		{
			((NormalizedList) value).changeListener = this;
		}

		return super.add(value);
	}

	@Override
	public void add(int index, Object value)
	{
		if (value instanceof NormalizedMap)
		{
			((NormalizedMap) value).changeListener = this;
		}
		else if (value instanceof NormalizedList)
		{
			((NormalizedList) value).changeListener = this;
		}

		super.add(index, value);
	}
}