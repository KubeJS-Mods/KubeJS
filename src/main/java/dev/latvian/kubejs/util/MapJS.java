package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MapJS extends LinkedHashMap<String, Object> implements WrappedJSObject, WrappedJSObjectChangeListener, Copyable, JsonSerializable, NBTSerializable
{
	@Nullable
	public static MapJS of(@Nullable Object o)
	{
		Object o1 = UtilsJS.wrap(o, JSObjectType.MAP);
		return o1 instanceof MapJS ? (MapJS) o1 : null;
	}

	@Nullable
	public static CompoundNBT nbt(@Nullable MapJS map)
	{
		return map == null ? null : map.toNBT();
	}

	public WrappedJSObjectChangeListener changeListener;

	public MapJS()
	{
	}

	public MapJS(int s)
	{
		super(s);
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

				if (o instanceof WrappedJSObject)
				{
					((WrappedJSObject) o).toString(builder);
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
	public MapJS copy()
	{
		MapJS map = new MapJS(size());

		for (Map.Entry<String, Object> entry : entrySet())
		{
			map.put(entry.getKey(), UtilsJS.copy(entry.getValue()));
		}

		return map;
	}

	@Override
	public void onChanged(@Nullable Object o)
	{
		if (changeListener != null)
		{
			changeListener.onChanged(this);
		}
	}

	@Override
	public Object put(String key, Object value)
	{
		Object v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (v instanceof MapJS)
		{
			((MapJS) v).changeListener = this;
		}
		else if (v instanceof ListJS)
		{
			((ListJS) v).changeListener = this;
		}

		return super.put(key, v);
	}

	@Override
	public void putAll(Map<? extends String, ?> m)
	{
		for (Map.Entry<?, ?> entry : m.entrySet())
		{
			put(entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = new JsonObject();

		for (Map.Entry<String, Object> entry : entrySet())
		{
			JsonElement e = JsonUtilsJS.of(entry.getValue());

			if (!e.isJsonNull())
			{
				json.add(entry.getKey(), e);
			}
		}

		return json;
	}

	@Override
	public CompoundNBT toNBT()
	{
		CompoundNBT nbt = new CompoundNBT();

		for (Map.Entry<String, Object> entry : entrySet())
		{
			INBT nbt1 = NBTUtilsJS.toNBT(entry.getValue());

			if (nbt1 != null)
			{
				nbt.put(entry.getKey(), nbt1);
			}
		}

		return nbt;
	}
}