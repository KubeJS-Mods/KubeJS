package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

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

	public static class OrderedCompoundTag extends CompoundTag {
		public OrderedCompoundTag() {
			super(new LinkedHashMap<>());
		}
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
}