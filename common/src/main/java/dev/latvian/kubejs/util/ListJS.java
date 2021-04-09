package dev.latvian.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class ListJS extends ArrayList<Object> implements WrappedJSObject, WrappedJSObjectChangeListener<Object>, Copyable, JsonSerializable, NBTSerializable {
	@Nullable
	public static ListJS of(@Nullable Object o) {
		Object o1 = UtilsJS.wrap(o, JSObjectType.LIST);
		return o1 instanceof ListJS ? (ListJS) o1 : null;
	}

	public static ListJS orSelf(@Nullable Object o) {
		ListJS l = of(o);

		if (l != null) {
			return l;
		}

		ListJS list = new ListJS(1);

		if (o != null) {
			list.add(o);
		}

		return list;
	}

	public static ListJS ofArray(Object array) {
		if (array instanceof Object[]) {
			ListJS list = new ListJS();
			Collections.addAll(list, (Object[]) array);
			return list;
		} else if (array instanceof int[]) {
			return ListJS.of((int[]) array);
		} else if (array instanceof byte[]) {
			return ListJS.of((byte[]) array);
		} else if (array instanceof short[]) {
			return ListJS.of((short[]) array);
		} else if (array instanceof long[]) {
			return ListJS.of((long[]) array);
		} else if (array instanceof float[]) {
			return ListJS.of((float[]) array);
		} else if (array instanceof double[]) {
			return ListJS.of((double[]) array);
		} else if (array instanceof char[]) {
			return ListJS.of((char[]) array);
		}

		return new ListJS(0);
	}

	public static ListJS of(byte[] array) {
		ListJS list = new ListJS(array.length);

		for (byte v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(short[] array) {
		ListJS list = new ListJS(array.length);

		for (short v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(int[] array) {
		ListJS list = new ListJS(array.length);

		for (int v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(long[] array) {
		ListJS list = new ListJS(array.length);

		for (long v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(float[] array) {
		ListJS list = new ListJS(array.length);

		for (float v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(double[] array) {
		ListJS list = new ListJS(array.length);

		for (double v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(char[] array) {
		ListJS list = new ListJS(array.length);

		for (char v : array) {
			list.add(v);
		}

		return list;
	}

	@Nullable
	public static JsonArray json(@Nullable Object array) {
		if (array instanceof JsonArray) {
			return (JsonArray) array;
		} else if (array instanceof CharSequence) {
			try {
				return JsonUtilsJS.GSON.fromJson(array.toString(), JsonArray.class);
			} catch (Exception ex) {
				return null;
			}
		}

		ListJS l = of(array);
		return l == null ? null : l.toJson();
	}

	@Nullable
	public static CollectionTag<?> nbt(@Nullable Object list) {
		if (list instanceof CollectionTag) {
			return (CollectionTag<?>) list;
		} else if (list instanceof CharSequence) {
			try {
				return (CollectionTag<?>) TagParser.parseTag("{a:" + list.toString() + "}").get("a");
			} catch (Exception ex) {
				return null;
			}
		}

		ListJS l = of(list);
		return l == null ? null : l.toNBT();
	}

	public WrappedJSObjectChangeListener<ListJS> changeListener;

	public ListJS() {
		this(0);
	}

	public ListJS(int s) {
		super(s);
	}

	public int getLength() {
		return size();
	}

	public ListJS push(Object... o) {
		if (o.length == 0) {
			return this;
		} else if (o.length == 1) {
			add(o[0]);
			return this;
		}

		for (int i = 0; i < o.length; i++) {
			o[i] = UtilsJS.wrap(o[i], JSObjectType.ANY);
			setChangeListener(o[i]);
		}

		super.addAll(Arrays.asList(o));
		onChanged(null);
		return this;
	}

	@Nullable
	public Object pop() {
		if (!isEmpty()) {
			return remove(size() - 1);
		}

		return null;
	}

	@Nullable
	public Object shift() {
		if (!isEmpty()) {
			return remove(0);
		}

		return null;
	}

	public ListJS unshift(Object... o) {
		if (o.length == 0) {
			return this;
		} else if (o.length == 1) {
			add(0, o[0]);
			return this;
		}

		for (int i = 0; i < o.length; i++) {
			o[i] = UtilsJS.wrap(o[i], JSObjectType.ANY);
			setChangeListener(o[i]);
		}

		super.addAll(0, Arrays.asList(o));
		onChanged(null);
		return this;
	}

	public ListJS reverse() {
		Collections.reverse(this);
		onChanged(null);
		return this;
	}

	public ListJS filter(Predicate<Object> predicate) {
		ListJS list = new ListJS();

		for (Object o : this) {
			if (predicate.test(o)) {
				list.add(o);
			}
		}

		return list;
	}

	public ListJS map(Function<Object, Object> transformer) {
		ListJS list = new ListJS();

		for (Object o : this) {
			list.add(transformer.apply(o));
		}

		return list;
	}

	public ListJS splice(int pos, int deleteCount, Object... items) {
		for (int i = 0; i < deleteCount; i++) {
			remove(pos);
		}

		if (items.length == 0) {
			return this;
		} else if (items.length == 1) {
			add(pos, items[0]);
			return this;
		}

		for (int i = 0; i < items.length; i++) {
			items[i] = UtilsJS.wrap(items[i], JSObjectType.ANY);
			setChangeListener(items[i]);
		}

		super.addAll(pos, Arrays.asList(items));
		onChanged(null);
		return this;
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}

		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(StringBuilder builder) {
		if (isEmpty()) {
			builder.append("[]");
			return;
		}

		builder.append('[');

		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				builder.append(',');
			}

			Object o = get(i);

			if (o instanceof WrappedJSObject) {
				((WrappedJSObject) o).toString(builder);
			} else {
				builder.append(o);
			}
		}

		builder.append(']');
	}

	@Override
	public ListJS getCopy() {
		ListJS list = new ListJS(size());

		for (Object object : this) {
			list.add(UtilsJS.copy(object));
		}

		return list;
	}

	protected boolean setChangeListener(@Nullable Object v) {
		if (v instanceof MapJS) {
			((MapJS) v).changeListener = this::onChanged;
		} else if (v instanceof ListJS) {
			((ListJS) v).changeListener = this::onChanged;
		}

		return true;
	}

	@Override
	public void onChanged(@Nullable Object o) {
		if (changeListener != null) {
			changeListener.onChanged(this);
		}
	}

	@Override
	public boolean add(Object value) {
		Object v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (setChangeListener(v)) {
			return super.add(v);
		}

		return false;
	}

	@Override
	public void add(int index, Object value) {
		Object v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (setChangeListener(v)) {
			super.add(index, v);
		}
	}

	@Override
	public boolean addAll(Collection c) {
		if (c == null || c.isEmpty()) {
			return false;
		}

		Object[] o = c.toArray();

		for (int i = 0; i < o.length; i++) {
			o[i] = UtilsJS.wrap(o[i], JSObjectType.ANY);
			setChangeListener(o[i]);
		}

		super.addAll(Arrays.asList(o));
		onChanged(null);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		if (c == null || c.isEmpty()) {
			return false;
		}

		Object[] o = c.toArray();

		for (int i = 0; i < o.length; i++) {
			o[i] = UtilsJS.wrap(o[i], JSObjectType.ANY);
			setChangeListener(o[i]);
		}

		super.addAll(index, Arrays.asList(o));
		onChanged(null);
		return true;
	}

	@Override
	public Object remove(int index) {
		Object o = super.remove(index);
		onChanged(null);
		return o;
	}

	@Override
	public boolean remove(Object o) {
		boolean b = super.remove(o);

		if (b) {
			onChanged(null);
		}

		return b;
	}

	@Override
	public void clear() {
		super.clear();
		onChanged(null);
	}

	@Override
	public JsonArray toJson() {
		JsonArray json = new JsonArray();

		for (Object o : this) {
			JsonElement e = JsonUtilsJS.of(o);

			if (!e.isJsonNull()) {
				json.add(e);
			}
		}

		return json;
	}

	@Override
	public CollectionTag<?> toNBT() {
		if (isEmpty()) {
			return new ListTag();
		}

		Tag[] values = new Tag[size()];
		int s = 0;
		byte commmonId = -1;

		for (Object o : this) {
			values[s] = NBTUtilsJS.toNBT(o);

			if (values[s] != null) {
				if (commmonId == -1) {
					commmonId = values[s].getId();
				} else if (commmonId != values[s].getId()) {
					commmonId = 0;
				}

				s++;
			}
		}

		if (commmonId == NbtType.INT) {
			int[] array = new int[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsInt();
			}

			return new IntArrayTag(array);
		} else if (commmonId == NbtType.BYTE) {
			byte[] array = new byte[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsByte();
			}

			return new ByteArrayTag(array);
		} else if (commmonId == NbtType.LONG) {
			long[] array = new long[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsLong();
			}

			return new LongArrayTag(array);
		} else if (commmonId == 0 || commmonId == -1) {
			return new ListTag();
		}

		ListTag nbt = new ListTag();

		for (Tag nbt1 : values) {
			if (nbt1 == null) {
				return nbt;
			}

			nbt.add(nbt1);
		}

		return nbt;
	}
}