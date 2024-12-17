package dev.latvian.mods.kubejs.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtFormatException;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NBTUtils {
	static void quoteAndEscape(StringBuilder stringBuilder, String string) {
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

	static Map<String, Tag> accessTagMap(CompoundTag tag) {
		return tag.tags;
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