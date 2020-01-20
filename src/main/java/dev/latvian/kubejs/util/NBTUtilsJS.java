package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;

import javax.annotation.Nullable;
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

		return MapJS.of(CompressedStreamTools.readCompressed(new FileInputStream(file)));
	}

	public static void write(File file, @Nullable MapJS nbt) throws IOException
	{
		KubeJS.verifyFilePath(file);

		if (nbt == null)
		{
			file.delete();
			return;
		}

		CompressedStreamTools.writeCompressed(nbt.toNBT(), new FileOutputStream(file));
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
	public static INBT toNBT(@Nullable Object o)
	{
		if (o instanceof NBTSerializable)
		{
			return ((NBTSerializable) o).toNBT();
		}
		else if (o instanceof String || o instanceof Character)
		{
			return StringNBT.of(o.toString());
		}
		else if (o instanceof Boolean)
		{
			return ByteNBT.of((Boolean) o ? (byte) 1 : (byte) 0);
		}
		else if (o instanceof Number)
		{
			Number number = (Number) o;

			if (number instanceof Byte)
			{
				return ByteNBT.of(number.byteValue());
			}
			else if (number instanceof Short)
			{
				return ShortNBT.of(number.shortValue());
			}
			else if (number instanceof Integer)
			{
				return IntNBT.of(number.intValue());
			}
			else if (number instanceof Float)
			{
				return FloatNBT.of(number.floatValue());
			}

			return DoubleNBT.of(number.doubleValue());
		}

		return null;
	}
}