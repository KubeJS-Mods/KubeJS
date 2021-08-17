package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.util.ListLike;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class CollectionTagWrapper<T extends Tag> implements ListLike<Object>, JsonSerializable, WrappedJSObjectChangeListener<Tag> {
	public final CollectionTag<T> minecraftTag;
	public WrappedJSObjectChangeListener<Tag> listener;

	public CollectionTagWrapper(CollectionTag<T> t) {
		minecraftTag = t;
	}

	@Override
	@Nullable
	public Object getLL(int index) {
		return CompoundTagWrapper.unwrap(minecraftTag.get(index), this);
	}

	@Override
	public void setLL(int index, Object value) {
		Tag t = CompoundTagWrapper.wrap(value);

		if (t != null) {
			minecraftTag.setTag(index, t);

			if (listener != null) {
				listener.onChanged(minecraftTag);
			}
		}
	}

	@Override
	public int sizeLL() {
		return minecraftTag.size();
	}

	public void add(Object value) {
		Tag t = CompoundTagWrapper.wrap(value);

		if (t != null) {
			minecraftTag.add((T) t);

			if (listener != null) {
				listener.onChanged(minecraftTag);
			}
		}
	}

	public void push(Object value) {
		add(value);
	}

	@Override
	public JsonArray toJson() {
		JsonArray json = new JsonArray();

		for (Tag tag : minecraftTag) {
			JsonElement e = JsonUtilsJS.of(CompoundTagWrapper.unwrap(tag, this));

			if (!e.isJsonNull()) {
				json.add(e);
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
