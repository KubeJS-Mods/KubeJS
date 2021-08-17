package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class NBTUtilsJS {
	@Nullable
	public static MapJS read(File file) throws IOException {
		KubeJS.verifyFilePath(file);

		if (!file.exists()) {
			return null;
		}

		return MapJS.of(NbtIo.readCompressed(new FileInputStream(file)));
	}

	public static void write(File file, @Nullable MapJS nbt) throws IOException {
		KubeJS.verifyFilePath(file);

		if (nbt == null) {
			file.delete();
			return;
		}

		NbtIo.writeCompressed(nbt.toNBT(), new FileOutputStream(file));
	}

	@Nullable
	public static MapJS read(String file) throws IOException {
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

	public static void write(String file, @Nullable MapJS nbt) throws IOException {
		write(KubeJS.getGameDirectory().resolve(file).toFile(), nbt);
	}

	@Nullable
	public static Tag toNBT(@Nullable Object o) {
		if (o instanceof Tag) {
			return (Tag) o;
		} else if (o instanceof NBTSerializable) {
			return ((NBTSerializable) o).toNBT();
		} else if (o instanceof CharSequence || o instanceof Character) {
			return StringTag.valueOf(o.toString());
		} else if (o instanceof Boolean) {
			return ByteTag.valueOf((Boolean) o ? (byte) 1 : (byte) 0);
		} else if (o instanceof Number) {
			Number number = (Number) o;

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
		} else if (o instanceof Map) {
			CompoundTag tag = new OrderedCompoundTag();

			for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
				Tag nbt1 = NBTUtilsJS.toNBT(entry.getValue());

				if (nbt1 != null) {
					tag.put(String.valueOf(entry.getKey()), nbt1);
				}
			}

			return tag;
		} else if (o instanceof Collection) {
			ListJS list = ListJS.of(o);

			if (list != null) {
				return list.toNBT();
			}
		}

		return null;
	}

	public static void quoteAndEscapeForJS(StringBuilder stringBuilder, String string) {
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

	private static TagType<?> convertType(TagType<?> tagType) {
		return tagType == CompoundTag.TYPE ? COMPOUND_TYPE : tagType == ListTag.TYPE ? LIST_TYPE : tagType;
	}

	private static final TagType<OrderedCompoundTag> COMPOUND_TYPE = new TagType<OrderedCompoundTag>() {
		public OrderedCompoundTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
			nbtAccounter.accountBits(384L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				Map<String, Tag> map = new LinkedHashMap<>();

				byte b;
				while ((b = dataInput.readByte()) != 0) {
					String string = dataInput.readUTF();
					nbtAccounter.accountBits(224L + 16L * string.length());
					TagType<?> tagType = convertType(TagTypes.getType(b));
					Tag tag = tagType.load(dataInput, i + 1, nbtAccounter);

					if (map.put(string, tag) != null) {
						nbtAccounter.accountBits(288L);
					}
				}

				return new OrderedCompoundTag(map);
			}
		}

		public String getName() {
			return "COMPOUND";
		}

		public String getPrettyName() {
			return "TAG_Compound";
		}
	};

	private static final TagType<ListTag> LIST_TYPE = new TagType<ListTag>() {
		public ListTag load(DataInput dataInput, int i, NbtAccounter nbtAccounter) throws IOException {
			nbtAccounter.accountBits(296L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				byte b = dataInput.readByte();
				int j = dataInput.readInt();
				if (b == 0 && j > 0) {
					throw new RuntimeException("Missing type on ListTag");
				} else {
					nbtAccounter.accountBits(32L * (long) j);
					TagType<?> tagType = convertType(TagTypes.getType(b));
					ListTag list = new ListTag();

					for (int k = 0; k < j; ++k) {
						list.add(tagType.load(dataInput, i + 1, nbtAccounter));
					}

					return list;
				}
			}
		}

		public String getName() {
			return "LIST";
		}

		public String getPrettyName() {
			return "TAG_List";
		}
	};

	@Nullable
	public static OrderedCompoundTag read(FriendlyByteBuf buf) {
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

					return COMPOUND_TYPE.load(stream, 0, NbtAccounter.UNLIMITED);
				}
			} catch (IOException var5) {
				throw new EncoderException(var5);
			}
		}
	}
}