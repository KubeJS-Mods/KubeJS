package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import dev.latvian.mods.rhino.mod.util.ChangeListener;
import dev.latvian.mods.rhino.mod.util.Copyable;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.StringBuilderAppendable;
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
public class ListJS extends ArrayList<Object> implements StringBuilderAppendable, ChangeListener<Object>, Copyable, JsonSerializable, NBTSerializable {
	@Nullable
	public static ListJS of(@Nullable Object o) {
		var o1 = UtilsJS.wrap(o, JSObjectType.LIST);
		return o1 instanceof ListJS ? (ListJS) o1 : null;
	}

	public static ListJS orSelf(@Nullable Object o) {
		var l = of(o);

		if (l != null) {
			return l;
		}

		var list = new ListJS(1);

		if (o != null) {
			list.add(o);
		}

		return list;
	}

	public static ListJS ofArray(Object array) {
		if (array instanceof Object[]) {
			var list = new ListJS();
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
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(short[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(int[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(long[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(float[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(double[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	public static ListJS of(char[] array) {
		var list = new ListJS(array.length);

		for (var v : array) {
			list.add(v);
		}

		return list;
	}

	@Nullable
	public static JsonArray json(@Nullable Object array) {
		if (array instanceof JsonArray arr) {
			return arr;
		} else if (array instanceof CharSequence) {
			try {
				return JsonIO.GSON.fromJson(array.toString(), JsonArray.class);
			} catch (Exception ex) {
				return null;
			}
		}

		var l = of(array);
		return l == null ? null : l.toJson();
	}

	@Nullable
	public static CollectionTag<?> nbt(@Nullable Object list) {
		if (list instanceof CollectionTag tag) {
			return tag;
		} else if (list instanceof CharSequence) {
			try {
				return (CollectionTag<?>) TagParser.parseTag("{a:" + list + "}").get("a");
			} catch (Exception ex) {
				return null;
			}
		}

		var l = of(list);
		return l == null ? null : l.toNBT();
	}

	public ChangeListener<ListJS> changeListener;

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

		for (var i = 0; i < o.length; i++) {
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

		for (var i = 0; i < o.length; i++) {
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
		var list = new ListJS();

		for (var o : this) {
			if (predicate.test(o)) {
				list.add(o);
			}
		}

		return list;
	}

	public ListJS map(Function<Object, Object> transformer) {
		var list = new ListJS();

		for (var o : this) {
			list.add(transformer.apply(o));
		}

		return list;
	}

	public ListJS splice(int pos, int deleteCount, Object... items) {
		for (var i = 0; i < deleteCount; i++) {
			remove(pos);
		}

		if (items.length == 0) {
			return this;
		} else if (items.length == 1) {
			add(pos, items[0]);
			return this;
		}

		for (var i = 0; i < items.length; i++) {
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

		var builder = new StringBuilder();
		appendString(builder);
		return builder.toString();
	}

	@Override
	public void appendString(StringBuilder builder) {
		if (isEmpty()) {
			builder.append("[]");
			return;
		}

		builder.append('[');

		for (var i = 0; i < size(); i++) {
			if (i > 0) {
				builder.append(',');
			}

			var o = get(i);

			if (o instanceof StringBuilderAppendable appendable) {
				appendable.appendString(builder);
			} else {
				builder.append(o);
			}
		}

		builder.append(']');
	}

	@Override
	public ListJS copy() {
		var list = new ListJS(size());

		for (var object : this) {
			list.add(UtilsJS.copy(object));
		}

		return list;
	}

	protected boolean setChangeListener(@Nullable Object v) {
		if (v instanceof MapJS map) {
			map.changeListener = this::onChanged;
		} else if (v instanceof ListJS list) {
			list.changeListener = this::onChanged;
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
		var v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (setChangeListener(v)) {
			return super.add(v);
		}

		return false;
	}

	@Override
	public void add(int index, Object value) {
		var v = UtilsJS.wrap(value, JSObjectType.ANY);

		if (setChangeListener(v)) {
			super.add(index, v);
		}
	}

	@Override
	public boolean addAll(Collection c) {
		if (c == null || c.isEmpty()) {
			return false;
		}

		var o = c.toArray();

		for (var i = 0; i < o.length; i++) {
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

		var o = c.toArray();

		for (var i = 0; i < o.length; i++) {
			o[i] = UtilsJS.wrap(o[i], JSObjectType.ANY);
			setChangeListener(o[i]);
		}

		super.addAll(index, Arrays.asList(o));
		onChanged(null);
		return true;
	}

	@Override
	public Object remove(int index) {
		var o = super.remove(index);
		onChanged(null);
		return o;
	}

	@Override
	public boolean remove(Object o) {
		var b = super.remove(o);

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
		var json = new JsonArray();

		for (var o : this) {
			var e = JsonIO.of(o);

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

		var values = new Tag[size()];
		var s = 0;
		byte commmonId = -1;

		for (var o : this) {
			values[s] = NBTUtils.toNBT(o);

			if (values[s] != null) {
				if (commmonId == -1) {
					commmonId = values[s].getId();
				} else if (commmonId != values[s].getId()) {
					commmonId = 0;
				}

				s++;
			}
		}

		if (commmonId == Tag.TAG_INT) {
			var array = new int[s];

			for (var i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsInt();
			}

			return new IntArrayTag(array);
		} else if (commmonId == Tag.TAG_BYTE) {
			var array = new byte[s];

			for (var i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsByte();
			}

			return new ByteArrayTag(array);
		} else if (commmonId == Tag.TAG_LONG) {
			var array = new long[s];

			for (var i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsLong();
			}

			return new LongArrayTag(array);
		} else if (commmonId == 0 || commmonId == -1) {
			return new ListTag();
		}

		var nbt = new ListTag();

		for (var nbt1 : values) {
			if (nbt1 == null) {
				return nbt;
			}

			nbt.add(nbt1);
		}

		return nbt;
	}
}