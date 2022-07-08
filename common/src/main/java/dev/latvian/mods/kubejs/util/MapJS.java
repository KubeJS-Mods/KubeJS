package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.util.ChangeListener;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.OrderedCompoundTag;
import dev.latvian.mods.rhino.mod.util.StringBuilderAppendable;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MapJS extends LinkedHashMap<String, Object> implements StringBuilderAppendable, ChangeListener<Object>, Copyable, JsonSerializable, NBTSerializable {
	@Nullable
	public static MapJS of(@Nullable Object o) {
		var o1 = UtilsJS.wrap(o, JSObjectType.MAP);
		return o1 instanceof MapJS ? (MapJS) o1 : null;
	}

	@Nullable
	@Deprecated
	public static CompoundTag nbt(@Nullable Object map) {
		return NBTUtils.toTagCompound(map);
	}

	@Nullable
	public static JsonObject json(@Nullable Object map) {
		if (map instanceof JsonObject json) {
			return json;
		} else if (map instanceof CharSequence) {
			try {
				return JsonIO.GSON.fromJson(map.toString(), JsonObject.class);
			} catch (Exception ex) {
				return null;
			}
		}

		var m = of(map);
		return m == null ? null : m.toJson();
	}

	public ChangeListener<MapJS> changeListener;

	public MapJS() {
		this(0);
	}

	public MapJS(int s) {
		super(s);
	}

	public int getLength() {
		return size();
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "{}";
		}

		var builder = new StringBuilder();
		appendString(builder);
		return builder.toString();
	}

	private boolean isWordChar(char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_';
	}

	private boolean isWordString(String s) {
		for (var i = 0; i < s.length(); i++) {
			if (!isWordChar(s.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void appendString(StringBuilder builder) {
		if (isEmpty()) {
			builder.append("{}");
			return;
		}

		builder.append('{');
		var first = true;

		for (var entry : entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			if (isWordString(entry.getKey())) {
				builder.append(entry.getKey());
			} else {
				builder.append('"');
				builder.append(entry.getKey().replace("\"", "\\\""));
				builder.append('"');
			}

			builder.append(':');

			if (entry.getValue() instanceof CharSequence) {
				builder.append('"');
				builder.append(entry.getValue().toString().replace("\"", "\\\""));
				builder.append('"');
			} else {
				var o = entry.getValue();

				if (o instanceof StringBuilderAppendable) {
					((StringBuilderAppendable) o).appendString(builder);
				} else {
					builder.append(o);
				}
			}
		}

		builder.append('}');
	}

	@Override
	public MapJS copy() {
		var map = new MapJS(size());

		for (var entry : entrySet()) {
			map.put(entry.getKey(), UtilsJS.copy(entry.getValue()));
		}

		return map;
	}

	protected boolean setChangeListener(@Nullable Object v) {
		if (v == null) {
			return false;
		} else if (v instanceof MapJS map) {
			map.changeListener = this::onChanged;
		}

		return true;
	}

	@Override
	public void onChanged(@Nullable Object o) {
		if (changeListener != null) {
			changeListener.onChanged(this);
		}
	}

	@Nullable
	private Object withChangeListener(Object value) {
		var v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (v instanceof Double d) {
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				return d;
			}

			if (d <= Integer.MAX_VALUE && d >= Integer.MIN_VALUE) {
				var i = d.intValue();

				if (i == d) {
					return i;
				}
			} else if (d <= Long.MAX_VALUE && d >= Long.MIN_VALUE) {
				var i = d.longValue();

				if (i == d) {
					return i;
				}
			}

			return d;
		}

		if (setChangeListener(v)) {
			return v;
		}

		return null;
	}

	@Override
	public Object put(String key, Object value) {
		var v = withChangeListener(value);

		if (v != null) {
			var o = super.put(key, v);
			onChanged(null);
			return o;
		}

		return null;
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		if (m == null || m.isEmpty()) {
			return;
		}

		for (Map.Entry<?, ?> entry : m.entrySet()) {
			var v = withChangeListener(entry.getValue());

			if (v != null) {
				super.put(entry.getKey().toString(), v);
			}
		}

		onChanged(null);
	}

	@Override
	public void clear() {
		super.clear();
		onChanged(null);
	}

	@Override
	@Nullable
	public Object remove(Object key) {
		var o = super.remove(key);

		if (o != null) {
			onChanged(null);
		}

		return o;
	}

	@Override
	public JsonObject toJson() {
		var json = new JsonObject();

		for (var entry : entrySet()) {
			var e = JsonIO.of(entry.getValue());
				json.add(entry.getKey(), e);
		}

		return json;
	}

	@Override
	public CompoundTag toNBT() {
		CompoundTag nbt = new OrderedCompoundTag();

		for (var entry : entrySet()) {
			var nbt1 = NBTUtils.toTag(entry.getValue());

			if (nbt1 != null) {
				nbt.put(entry.getKey(), nbt1);
			}
		}

		return nbt;
	}
}