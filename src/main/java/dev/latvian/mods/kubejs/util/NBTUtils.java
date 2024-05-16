package dev.latvian.mods.kubejs.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.ValueUnwrapper;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
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
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtFormatException;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NBTUtils {
	ValueUnwrapper VALUE_UNWRAPPER = (contextData, scope, value) -> value instanceof Tag tag ? fromTag(tag) : value;

	@Nullable
	static Object fromTag(@Nullable Tag t) {
		if (t == null || t instanceof EndTag) {
			return null;
		} else if (t instanceof StringTag) {
			return t.getAsString();
		} else if (t instanceof NumericTag num) {
			return num.getAsNumber();
		}

		return t;
	}

	@Nullable
	static Tag toTag(Context cx, @Nullable Object v) {
		if (v == null || v instanceof EndTag) {
			return null;
		} else if (v instanceof Tag tag) {
			return tag;
		} else if (v instanceof NBTSerializable s) {
			return s.toNBTJS(cx);
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
				return toTag(cx, json.getAsNumber());
			} else if (json.isBoolean()) {
				return ByteTag.valueOf(json.getAsBoolean());
			} else {
				return StringTag.valueOf(json.getAsString());
			}
		} else if (v instanceof Map<?, ?> map) {
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Tag nbt1 = toTag(cx, entry.getValue());

				if (nbt1 != null) {
					tag.put(String.valueOf(entry.getKey()), nbt1);
				}
			}

			return tag;
		} else if (v instanceof JsonObject json) {
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				Tag nbt1 = toTag(cx, entry.getValue());

				if (nbt1 != null) {
					tag.put(entry.getKey(), nbt1);
				}
			}

			return tag;
		} else if (v instanceof Collection<?> c) {
			return toTagCollection(cx, c);
		} else if (v instanceof JsonArray array) {
			List<Tag> list = new ArrayList<>(array.size());

			for (JsonElement element : array) {
				list.add(toTag(cx, element));
			}

			return toTagCollection(cx, list);
		}

		return null;
	}

	static boolean isTagCompound(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CompoundTag || o instanceof CharSequence || o instanceof Map || o instanceof JsonElement;
	}

	@Nullable
	static CompoundTag toTagCompound(Context cx, @Nullable Object v) {
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

		return toTag(cx, v) instanceof CompoundTag nbt ? nbt : null;
	}

	static boolean isTagCollection(Object o) {
		return o == null || Undefined.isUndefined(o) || o instanceof CharSequence || o instanceof Collection<?> || o instanceof JsonArray;
	}

	@Nullable
	static CollectionTag<?> toTagCollection(Context cx, @Nullable Object v) {
		if (v instanceof CollectionTag tag) {
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
				list.add(toTag(cx, element));
			}

			return toTagCollection(cx, list);
		}

		return v == null ? null : toTagCollection(cx, (Collection<?>) v);
	}

	@Nullable
	static ListTag toTagList(Context cx, @Nullable Object list) {
		return (ListTag) toTagCollection(cx, list);
	}

	static CollectionTag<?> toTagCollection(Context cx, Collection<?> c) {
		if (c.isEmpty()) {
			return new ListTag();
		}

		Tag[] values = new Tag[c.size()];
		int s = 0;
		byte commmonId = -1;

		for (Object o : c) {
			values[s] = toTag(cx, o);

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
			var tag1 = toTag(cx, entry.getValue());

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
			tag.add(toTag(cx, v));
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

	static void quoteAndEscapeForJS(StringBuilder stringBuilder, String string) {
		int start = stringBuilder.length();
		stringBuilder.append(' ');
		char c = 0;

		for (int i = 0; i < string.length(); ++i) {
			char d = string.charAt(i);
			if (d == '\\') {
				stringBuilder.append('\\');
			} else if (d == '"' || d == '\'') {
				if (c == 0) {
					c = d == '\'' ? '"' : '\'';
				}

				if (c == d) {
					stringBuilder.append('\\');
				}
			}

			stringBuilder.append(d);
		}

		if (c == 0) {
			c = '\'';
		}

		stringBuilder.setCharAt(start, c);
		stringBuilder.append(c);
	}

	static TagType<?> convertType(TagType<?> tagType) {
		return tagType == CompoundTag.TYPE ? COMPOUND_TYPE : tagType == ListTag.TYPE ? LIST_TYPE : tagType;
	}

	static JsonElement toJson(@Nullable Tag t) {
		if (t == null || t instanceof EndTag) {
			return JsonNull.INSTANCE;
		} else if (t instanceof StringTag) {
			return new JsonPrimitive(t.getAsString());
		} else if (t instanceof NumericTag) {
			return new JsonPrimitive(((NumericTag) t).getAsNumber());
		} else if (t instanceof CollectionTag<?> l) {
			JsonArray array = new JsonArray();

			for (Tag tag : l) {
				array.add(toJson(tag));
			}

			return array;
		} else if (t instanceof CompoundTag c) {
			JsonObject object = new JsonObject();

			for (String key : c.getAllKeys()) {
				object.add(key, toJson(c.get(key)));
			}

			return object;
		}

		return JsonNull.INSTANCE;
	}

	@Nullable
	static OrderedCompoundTag read(FriendlyByteBuf buf) {
		int i = buf.readerIndex();
		byte b = buf.readByte();
		if (b == 0) {
			return null;
		} else {
			buf.readerIndex(i);

			try {
				DataInputStream stream = new DataInputStream(new ByteBufInputStream(buf));

				byte b1 = stream.readByte();
				if (b1 == 0) {
					return null;
				} else {
					stream.readUTF();
					TagType<?> tagType = convertType(TagTypes.getType(b1));

					if (tagType != COMPOUND_TYPE) {
						return null;
					}

					return COMPOUND_TYPE.load(stream, NbtAccounter.unlimitedHeap());
				}
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}

	TagType<OrderedCompoundTag> COMPOUND_TYPE = new TagType.VariableSize<>() {
		@Override
		public OrderedCompoundTag load(DataInput dataInput, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			try {
				accounter.accountBytes(48L);
				Map<String, Tag> map = new LinkedHashMap<>();

				byte typeId;
				while ((typeId = dataInput.readByte()) != 0) {
					String key = readString(dataInput, accounter);
					TagType<?> valueType = convertType(TagTypes.getType(typeId));
					Tag tag = CompoundTag.readNamedTagData(valueType, key, dataInput, accounter);
					if (map.put(key, tag) == null) {
						accounter.accountBytes(36L);
					}
				}

				return new OrderedCompoundTag(map);
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public StreamTagVisitor.ValueResult parse(DataInput dataInput, StreamTagVisitor visitor, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			try {
				accounter.accountBytes(48L);

				while (true) {
					byte typeId;
					if ((typeId = dataInput.readByte()) != 0) {
						TagType<?> valueType = convertType(TagTypes.getType(typeId));
						switch (visitor.visitEntry(valueType)) {
							case HALT:
								return StreamTagVisitor.ValueResult.HALT;
							case BREAK:
								StringTag.skipString(dataInput);
								valueType.skip(dataInput, accounter);
								break;
							case SKIP:
								StringTag.skipString(dataInput);
								valueType.skip(dataInput, accounter);
								continue;
							default:
								String key = readString(dataInput, accounter);
								switch (visitor.visitEntry(valueType, key)) {
									case HALT:
										return StreamTagVisitor.ValueResult.HALT;
									case BREAK:
										valueType.skip(dataInput, accounter);
										break;
									case SKIP:
										valueType.skip(dataInput, accounter);
										continue;
									default:
										accounter.accountBytes(36L);
										switch (valueType.parse(dataInput, visitor, accounter)) {
											case HALT:
												return StreamTagVisitor.ValueResult.HALT;
											case BREAK:
											default:
												continue;
										}
								}
						}
					}

					if (typeId != 0) {
						while ((typeId = dataInput.readByte()) != 0) {
							StringTag.skipString(dataInput);
							convertType(TagTypes.getType(typeId)).skip(dataInput, accounter);
						}
					}

					return visitor.visitContainerEnd();
				}
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public void skip(DataInput dataInput, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			byte typeId;
			try {
				while ((typeId = dataInput.readByte()) != 0) {
					StringTag.skipString(dataInput);
					convertType(TagTypes.getType(typeId)).skip(dataInput, accounter);
				}
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public String getName() {
			return "COMPOUND";
		}

		@Override
		public String getPrettyName() {
			return "TAG_Compound";
		}

		private static String readString(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
			String string = dataInput.readUTF();
			nbtAccounter.accountBytes(28L);
			nbtAccounter.accountBytes(2L, string.length());
			return string;
		}
	};

	static Map<String, Tag> accessTagMap(CompoundTag tag) {
		return tag.tags;
	}

	TagType<ListTag> LIST_TYPE = new TagType.VariableSize<>() {
		@Override
		public ListTag load(DataInput dataInput, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			try {
				accounter.accountBytes(37L);
				byte typeId = dataInput.readByte();
				int size = dataInput.readInt();
				if (typeId == 0 && size > 0) {
					throw new NbtFormatException("Missing type on ListTag");
				} else {
					accounter.accountBytes(4L, size);
					TagType<?> valueType = convertType(TagTypes.getType(typeId));
					List<Tag> list = Lists.newArrayListWithCapacity(size);

					for (int j = 0; j < size; ++j) {
						list.add(valueType.load(dataInput, accounter));
					}

					return new ListTag(list, typeId);
				}
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public StreamTagVisitor.ValueResult parse(DataInput dataInput, StreamTagVisitor visitor, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			try {
				accounter.accountBytes(37L);
				TagType<?> tagType = convertType(TagTypes.getType(dataInput.readByte()));
				int size = dataInput.readInt();
				switch (visitor.visitList(tagType, size)) {
					case HALT:
						return StreamTagVisitor.ValueResult.HALT;
					case BREAK:
						tagType.skip(dataInput, size, accounter);
						return visitor.visitContainerEnd();
					default:
						accounter.accountBytes(4L, size);
						int i = 0;

						out:
						for (; i < size; ++i) {
							switch (visitor.visitElement(tagType, i)) {
								case HALT:
									return StreamTagVisitor.ValueResult.HALT;
								case BREAK:
									tagType.skip(dataInput, accounter);
									break out;
								case SKIP:
									tagType.skip(dataInput, accounter);
									break;
								default:
									switch (tagType.parse(dataInput, visitor, accounter)) {
										case HALT:
											return StreamTagVisitor.ValueResult.HALT;
										case BREAK:
											break out;
									}
							}
						}

						int toSkip = size - 1 - i;
						if (toSkip > 0) {
							tagType.skip(dataInput, toSkip, accounter);
						}

						return visitor.visitContainerEnd();
				}
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public void skip(DataInput dataInput, NbtAccounter accounter) throws IOException {
			accounter.pushDepth();

			try {
				TagType<?> tagType = convertType(TagTypes.getType(dataInput.readByte()));
				int i = dataInput.readInt();
				tagType.skip(dataInput, i, accounter);
			} finally {
				accounter.popDepth();
			}
		}

		@Override
		public String getName() {
			return "LIST";
		}

		@Override
		public String getPrettyName() {
			return "TAG_List";
		}
	};
}