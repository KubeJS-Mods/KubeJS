package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
}