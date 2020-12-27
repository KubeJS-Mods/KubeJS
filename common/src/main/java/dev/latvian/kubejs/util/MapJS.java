package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.CompoundNBTKJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MapJS extends LinkedHashMap<String, Object> implements WrappedJSObject, WrappedJSObjectChangeListener<Object>, Copyable, JsonSerializable, NBTSerializable
{
	@Nullable
	public static MapJS of(@Nullable Object o)
	{
		Object o1 = UtilsJS.wrap(o, JSObjectType.MAP);
		return o1 instanceof MapJS ? (MapJS) o1 : null;
	}

	@Nullable
	public static CompoundTag nbt(@Nullable Object map)
	{
		if (map instanceof CompoundTag)
		{
			return (CompoundTag) map;
		}
		else if (map instanceof CharSequence)
		{
			try
			{
				return TagParser.parseTag(map.toString());
			}
			catch (Exception ex)
			{
				return null;
			}
		}

		MapJS m = of(map);
		return m == null ? null : m.toNBT();
	}

	public WrappedJSObjectChangeListener<MapJS> changeListener;

	public MapJS()
	{
		this(0);
	}

	public MapJS(int s)
	{
		super(s);
	}

	public int getLength()
	{
		return size();
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

	private boolean isWordChar(char c)
	{
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_';
	}

	private boolean isWordString(String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			if (!isWordChar(s.charAt(i)))
			{
				return false;
			}
		}

		return true;
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

			if (isWordString(entry.getKey()))
			{
				builder.append(entry.getKey());
			}
			else
			{
				builder.append('"');
				builder.append(entry.getKey().replace("\"", "\\\""));
				builder.append('"');
			}

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

	protected boolean setChangeListener(@Nullable Object v)
	{
		if (v == null)
		{
			return false;
		}
		else if (v instanceof MapJS)
		{
			((MapJS) v).changeListener = this::onChanged;
		}
		else if (v instanceof ListJS)
		{
			((ListJS) v).changeListener = this::onChanged;
		}

		return true;
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

		if (setChangeListener(v))
		{
			Object o = super.put(key, v);
			onChanged(null);
			return o;
		}

		return null;
	}

	@Override
	public void putAll(Map<? extends String, ?> m)
	{
		if (m == null || m.isEmpty())
		{
			return;
		}

		for (Map.Entry<?, ?> entry : m.entrySet())
		{
			Object v = UtilsJS.wrap(entry.getValue(), JSObjectType.ANY);

			if (setChangeListener(v))
			{
				super.put(entry.getKey().toString(), v);
			}
		}

		onChanged(null);
	}

	@Override
	public void clear()
	{
		super.clear();
		onChanged(null);
	}

	@Override
	@Nullable
	public Object remove(Object key)
	{
		Object o = super.remove(key);

		if (o != null)
		{
			onChanged(null);
		}

		return o;
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
	public CompoundTag toNBT()
	{
		CompoundTag nbt = new CompoundTag();
		//noinspection ConstantConditions
		((CompoundNBTKJS) nbt).setTagsKJS(new LinkedHashMap<>());

		for (Map.Entry<String, Object> entry : entrySet())
		{
			Tag nbt1 = NBTUtilsJS.toNBT(entry.getValue());

			if (nbt1 != null)
			{
				nbt.put(entry.getKey(), nbt1);
			}
		}

		return nbt;
	}

	public MapJS getOrNewMap(String id)
	{
		MapJS map = of(get(id));

		if (map == null)
		{
			map = new MapJS();
			put(id, map);
		}

		return map;
	}

	public ListJS getOrNewList(String id)
	{
		ListJS list = ListJS.of(get(id));

		if (list == null)
		{
			list = new ListJS();
			put(id, list);
		}

		return list;
	}
}