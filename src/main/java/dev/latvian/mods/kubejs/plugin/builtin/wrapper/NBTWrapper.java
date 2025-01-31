package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.util.NBTSerializable;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.OrderedCompoundTag;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NBTWrapper {
	static boolean isTagCompound(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CompoundTag || o instanceof CharSequence || o instanceof Map || o instanceof JsonElement;
	}

	static boolean isTagCollection(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CharSequence || o instanceof Collection<?> || o instanceof JsonArray;
	}

	@Nullable
	static Object fromTag(@Nullable Tag t) {
		return switch (t) {
			case null -> null;
			case EndTag ignore -> null;
			case StringTag ignore -> t.getAsString();
			case NumericTag num -> num.getAsNumber();
			case CompoundTag tag -> {
				if (tag.isEmpty()) {
					yield Map.of();
				}

				var map0 = NBTUtils.accessTagMap(tag);
				var map = new LinkedHashMap<String, Object>(map0.size());

				for (var entry : map0.entrySet()) {
					map.put(entry.getKey(), fromTag(entry.getValue()));
				}

				yield map;
			}
			case CollectionTag<?> tag -> {
				if (tag.isEmpty()) {
					yield List.of();
				}

				var list = new ArrayList<>(tag.size());

				for (var v : tag) {
					list.add(fromTag(v));
				}

				yield list;
			}
			default -> t;
		};
	}

	@Nullable
	static Tag toTag(@Nullable Tag tag) {
		return tag;
	}

	@Nullable
	static Tag wrap(Context cx, @Nullable Object v) {
		if (v == null || v instanceof EndTag) {
			return null;
		} else if (v instanceof Tag tag) {
			return tag;
		} else if (v instanceof NBTSerializable s) {
			return s.toNBT(cx);
		} else if (v instanceof CharSequence || v instanceof Character) {
			return StringTag.valueOf(v.toString());
		} else if (v instanceof Boolean b) {
			return ByteTag.valueOf(b);
		} else if (v instanceof Number number) {
			if (number instanceof Byte) {
				return ByteTag.valueOf(number.byteValue());
			} else if (number instanceof Short) {
				return ShortTag.valueOf(number.shortValue());
			} else if (number instanceof Integer) {
				return IntTag.valueOf(number.intValue());
			} else if (number instanceof Long) {
				return LongTag.valueOf(number.longValue());
			} else if (number instanceof Float) {
				return FloatTag.valueOf(number.floatValue());
			}

			return DoubleTag.valueOf(number.doubleValue());
		} else if (v instanceof JsonPrimitive json) {
			if (json.isNumber()) {
				return wrap(cx, json.getAsNumber());
			} else if (json.isBoolean()) {
				return ByteTag.valueOf(json.getAsBoolean());
			} else {
				return StringTag.valueOf(json.getAsString());
			}
		} else if (v instanceof Map<?, ?> map) {
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Tag nbt1 = wrap(cx, entry.getValue());

				if (nbt1 != null) {
					tag.put(String.valueOf(entry.getKey()), nbt1);
				}
			}

			return tag;
		} else if (v instanceof JsonObject json) {
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				Tag nbt1 = wrap(cx, entry.getValue());

				if (nbt1 != null) {
					tag.put(entry.getKey(), nbt1);
				}
			}

			return tag;
		} else if (v instanceof Collection<?> c) {
			return wrapCollection0(cx, c);
		} else if (v instanceof JsonArray array) {
			List<Tag> list = new ArrayList<>(array.size());

			for (JsonElement element : array) {
				list.add(wrap(cx, element));
			}

			return wrapCollection0(cx, list);
		}

		return null;
	}

	@Nullable
	static CompoundTag wrapCompound(Context cx, @Nullable Object v) {
		if (v instanceof CompoundTag nbt) {
			return nbt;
		} else if (v instanceof CharSequence) {
			try {
				return TagParser.parseTag(v.toString());
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonPrimitive json) {
			try {
				return TagParser.parseTag(json.getAsString());
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonObject json) {
			try {
				return TagParser.parseTag(json.toString());
			} catch (Exception ex) {
				return null;
			}
		}

		return wrap(cx, v) instanceof CompoundTag nbt ? nbt : null;
	}

	@Nullable
	static CollectionTag<?> wrapCollection(Context cx, @Nullable Object v) {
		if (v instanceof CollectionTag<?> tag) {
			return tag;
		} else if (v instanceof CharSequence) {
			try {
				return (CollectionTag<?>) TagParser.parseTag("{a:" + v + "}").get("a");
			} catch (Exception ex) {
				return null;
			}
		} else if (v instanceof JsonArray array) {
			List<Tag> list = new ArrayList<>(array.size());

			for (JsonElement element : array) {
				list.add(wrap(cx, element));
			}

			return wrapCollection0(cx, list);
		}

		return v == null ? null : wrapCollection0(cx, (Collection<?>) v);
	}

	@Nullable
	static ListTag wrapListTag(Context cx, @Nullable Object list) {
		return (ListTag) wrapCollection(cx, list);
	}

	private static CollectionTag<?> wrapCollection0(Context cx, Collection<?> c) {
		if (c.isEmpty()) {
			return new ListTag();
		}

		Tag[] values = new Tag[c.size()];
		int s = 0;
		byte commmonId = -1;

		for (Object o : c) {
			values[s] = wrap(cx, o);

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
			int[] array = new int[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsInt();
			}

			return new IntArrayTag(array);
		} else if (commmonId == Tag.TAG_BYTE) {
			byte[] array = new byte[s];

			for (int i = 0; i < s; i++) {
				array[i] = ((NumericTag) values[i]).getAsByte();
			}

			return new ByteArrayTag(array);
		} else if (commmonId == Tag.TAG_LONG) {
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

	static Tag compoundTag() {
		return new OrderedCompoundTag();
	}

	static Tag compoundTag(Context cx, Map<?, ?> map) {
		OrderedCompoundTag tag = new OrderedCompoundTag();

		for (var entry : map.entrySet()) {
			var tag1 = wrap(cx, entry.getValue());

			if (tag1 != null) {
				tag.put(String.valueOf(entry.getKey()), tag1);
			}
		}

		return tag;
	}

	static Tag listTag() {
		return new ListTag();
	}

	static Tag listTag(Context cx, List<?> list) {
		ListTag tag = new ListTag();

		for (Object v : list) {
			tag.add(wrap(cx, v));
		}

		return tag;
	}

	static Tag byteTag(byte v) {
		return ByteTag.valueOf(v);
	}

	static Tag b(byte v) {
		return ByteTag.valueOf(v);
	}

	static Tag shortTag(short v) {
		return ShortTag.valueOf(v);
	}

	static Tag s(short v) {
		return ShortTag.valueOf(v);
	}

	static Tag intTag(int v) {
		return IntTag.valueOf(v);
	}

	static Tag i(int v) {
		return IntTag.valueOf(v);
	}

	static Tag longTag(long v) {
		return LongTag.valueOf(v);
	}

	static Tag l(long v) {
		return LongTag.valueOf(v);
	}

	static Tag floatTag(float v) {
		return FloatTag.valueOf(v);
	}

	static Tag f(float v) {
		return FloatTag.valueOf(v);
	}

	static Tag doubleTag(double v) {
		return DoubleTag.valueOf(v);
	}

	static Tag d(double v) {
		return DoubleTag.valueOf(v);
	}

	static Tag stringTag(String v) {
		return StringTag.valueOf(v);
	}

	static Tag intArrayTag(int[] v) {
		return new IntArrayTag(v);
	}

	static Tag ia(int[] v) {
		return new IntArrayTag(v);
	}

	static Tag longArrayTag(long[] v) {
		return new LongArrayTag(v);
	}

	static Tag la(long[] v) {
		return new LongArrayTag(v);
	}

	static Tag byteArrayTag(byte[] v) {
		return new ByteArrayTag(v);
	}

	static Tag ba(byte[] v) {
		return new ByteArrayTag(v);
	}

	static JsonElement toJson(@Nullable Tag t) {
		return NBTUtils.toJson(t);
	}

	@Nullable
	static OrderedCompoundTag read(FriendlyByteBuf buf) {
		return NBTUtils.read(buf);
	}
}
