package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author LatvianModder
 */
public class ListJS extends ArrayList<Object> implements WrappedJSObject, Copyable, JSObjectChangeListener, JsonSerializable
{
	@Nullable
	public static ListJS of(@Nullable Object o)
	{
		Object o1 = UtilsJS.wrap(o, JSObjectType.LIST);
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

	public static ListJS ofArray(Object array)
	{
		if (array instanceof Object[])
		{
			ListJS list = new ListJS();
			Collections.addAll(list, (Object[]) array);
			return list;
		}
		else if (array instanceof int[])
		{
			return ListJS.of((int[]) array);
		}
		else if (array instanceof byte[])
		{
			return ListJS.of((byte[]) array);
		}
		else if (array instanceof short[])
		{
			return ListJS.of((short[]) array);
		}
		else if (array instanceof long[])
		{
			return ListJS.of((long[]) array);
		}
		else if (array instanceof float[])
		{
			return ListJS.of((float[]) array);
		}
		else if (array instanceof double[])
		{
			return ListJS.of((double[]) array);
		}
		else if (array instanceof char[])
		{
			return ListJS.of((char[]) array);
		}

		return new ListJS(0);
	}

	public static ListJS of(byte[] array)
	{
		ListJS list = new ListJS(array.length);

		for (byte v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(short[] array)
	{
		ListJS list = new ListJS(array.length);

		for (short v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(int[] array)
	{
		ListJS list = new ListJS(array.length);

		for (int v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(long[] array)
	{
		ListJS list = new ListJS(array.length);

		for (long v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(float[] array)
	{
		ListJS list = new ListJS(array.length);

		for (float v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(double[] array)
	{
		ListJS list = new ListJS(array.length);

		for (double v : array)
		{
			list.add(v);
		}

		return list;
	}

	public static ListJS of(char[] array)
	{
		ListJS list = new ListJS(array.length);

		for (char v : array)
		{
			list.add(v);
		}

		return list;
	}

	public JSObjectChangeListener changeListener;

	public ListJS()
	{
	}

	public ListJS(int s)
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

			if (o instanceof WrappedJSObject)
			{
				((WrappedJSObject) o).toString(builder);
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
		Object v = UtilsJS.wrap(value, JSObjectType.ANY);

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
		Object v = UtilsJS.wrap(value, JSObjectType.ANY);

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

	@Override
	public JsonArray getJson()
	{
		JsonArray json = new JsonArray();

		for (Object o : this)
		{
			JsonElement e = JsonUtilsJS.of(o);

			if (!e.isJsonNull())
			{
				json.add(e);
			}
		}

		return json;
	}
}