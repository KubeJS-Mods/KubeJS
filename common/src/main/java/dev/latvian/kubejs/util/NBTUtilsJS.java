package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class NBTUtilsJS
{
	@Nullable
	public static MapJS read(File file) throws IOException
	{
		KubeJS.verifyFilePath(file);

		if (!file.exists())
		{
			return null;
		}

		return MapJS.of(NbtIo.readCompressed(new FileInputStream(file)));
	}

	public static void write(File file, @Nullable MapJS nbt) throws IOException
	{
		KubeJS.verifyFilePath(file);

		if (nbt == null)
		{
			file.delete();
			return;
		}

		NbtIo.writeCompressed(nbt.toNBT(), new FileOutputStream(file));
	}

	@Nullable
	public static MapJS read(String file) throws IOException
	{
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

	public static void write(String file, @Nullable MapJS nbt) throws IOException
	{
		write(KubeJS.getGameDirectory().resolve(file).toFile(), nbt);
	}

	@Nullable
	public static Tag toNBT(@Nullable Object o)
	{
		if (o instanceof NBTSerializable)
		{
			return ((NBTSerializable) o).toNBT();
		}
		else if (o instanceof String || o instanceof Character)
		{
			return StringTag.valueOf(o.toString());
		}
		else if (o instanceof Boolean)
		{
			return ByteTag.valueOf((Boolean) o ? (byte) 1 : (byte) 0);
		}
		else if (o instanceof Number)
		{
			Number number = (Number) o;

			if (number instanceof Byte)
			{
				return ByteTag.valueOf(number.byteValue());
			}
			else if (number instanceof Short)
			{
				return ShortTag.valueOf(number.shortValue());
			}
			else if (number instanceof Integer)
			{
				return IntTag.valueOf(number.intValue());
			}
			else if (number instanceof Float)
			{
				return FloatTag.valueOf(number.floatValue());
			}

			return DoubleTag.valueOf(number.doubleValue());
		}

		return null;
	}
}