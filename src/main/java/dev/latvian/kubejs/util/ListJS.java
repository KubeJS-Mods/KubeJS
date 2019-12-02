package dev.latvian.kubejs.util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public class ListJS extends ArrayList<Object> implements Normalized, Copyable, JSObjectChangeListener
{
	@Nullable
	public static ListJS of(@Nullable Object o)
	{
		Object o1 = UtilsJS.normalize(o);
		return o1 instanceof ListJS ? (ListJS) o1 : null;
	}

	public static ListJS orSelf(@Nullable Object o)
	{
		ListJS l = of(o);

		if (l != null)
		{
			return l;
		}

		ListJS list = new ListJS(1);

		if (o != null)
		{
			list.add(o);
		}

		return list;
	}

	public JSObjectChangeListener changeListener;

	ListJS()
	{
	}

	ListJS(int s)
	{
		super(s);
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
	public ListJS copy()
	{
		ListJS list = new ListJS(size());

		for (Object object : this)
		{
			list.add(UtilsJS.copy(object));
		}

		return list;
	}

	@Override
	public void onChanged(Object o)
	{
		if (changeListener != null)
		{
			changeListener.onChanged(this);
		}
	}

	@Override
	public boolean add(Object value)
	{
		Object v = UtilsJS.normalize(value);

		if (v == null)
		{
			return false;
		}
		else if (v instanceof MapJS)
		{
			((MapJS) v).changeListener = this;
		}
		else if (v instanceof ListJS)
		{
			((ListJS) v).changeListener = this;
		}

		return super.add(v);
	}

	@Override
	public void add(int index, Object value)
	{
		Object v = UtilsJS.normalize(value);

		if (v == null)
		{
			return;
		}
		else if (v instanceof MapJS)
		{
			((MapJS) v).changeListener = this;
		}
		else if (v instanceof ListJS)
		{
			((ListJS) v).changeListener = this;
		}

		super.add(index, v);
	}

	@Override
	public boolean addAll(Collection c)
	{
		for (Object o : c)
		{
			add(o);
		}

		return true;
	}
}