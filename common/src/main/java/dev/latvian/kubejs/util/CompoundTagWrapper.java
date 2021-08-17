package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.util.MapLike;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CompoundTagWrapper implements MapLike<String, Object>, JsonSerializable, WrappedJSObjectChangeListener<Tag> {
	public static Object unwrap(@Nullable Tag t, @Nullable WrappedJSObjectChangeListener<Tag> l) {
		if (t == null || t instanceof EndTag) {
			return null;
		} else if (t instanceof StringTag) {
			return t.getAsString();
		} else if (t instanceof NumericTag) {
			return ((NumericTag) t).getAsNumber();
		} else if (t instanceof CompoundTag) {
			CompoundTagWrapper c = new CompoundTagWrapper((CompoundTag) t);
			c.listener = l;
			return c;
		} else if (t instanceof CollectionTag) {
			CollectionTagWrapper<?> c = new CollectionTagWrapper<>((CollectionTag<?>) t);
			c.listener = l;
			return c;
		}

		return t;
	}

	public static Tag wrap(@Nullable Object o) {
		if (o instanceof Tag) {
			return (Tag) o;
		} else if (o instanceof Number) {
			return DoubleTag.valueOf(((Number) o).doubleValue());
		} else if (o instanceof CharSequence) {
			return StringTag.valueOf(o.toString());
		} else if (o instanceof CompoundTagWrapper) {
			return ((CompoundTagWrapper) o).minecraftTag;
		} else if (o instanceof CollectionTagWrapper) {
			return ((CollectionTagWrapper<?>) o).minecraftTag;
		}

		return null;
	}

	public final CompoundTag minecraftTag;
	public WrappedJSObjectChangeListener<Tag> listener;

	public CompoundTagWrapper(CompoundTag t) {
		minecraftTag = t;
	}

	@Override
	public Object getML(String key) {
		return unwrap(minecraftTag.get(key), this);
	}

	@Override
	public void putML(String key, Object value) {
		Tag t = wrap(value);

		if (t != null) {
			minecraftTag.put(key, t);

			if (listener != null) {
				listener.onChanged(minecraftTag);
			}
		}
	}

	@Override
	public boolean containsKeyML(String key) {
		return minecraftTag.contains(key);
	}

	@Override
	public Collection<String> keysML() {
		return minecraftTag.getAllKeys();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		for (String key : minecraftTag.getAllKeys()) {
			JsonElement e = JsonUtilsJS.of(unwrap(minecraftTag.get(key), this));

			if (!e.isJsonNull()) {
				json.add(key, e);
			}
		}

		return json;
	}

	@Override
	public void onChanged(Tag o) {
		if (listener != null) {
			listener.onChanged(minecraftTag);
		}
	}
}
