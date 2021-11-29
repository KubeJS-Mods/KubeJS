package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTUtilsJS;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class NBTIOWrapper {
	@Nullable
	public static MapJS read(File file) throws IOException {
		return NBTUtilsJS.read(file);
	}

	public static void write(File file, Object nbt) throws IOException {
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}

	@Nullable
	public static Object read(String file) throws IOException {
		return NBTUtilsJS.read(file);
	}

	public static void write(String file, Object nbt) throws IOException {
		NBTUtilsJS.write(file, MapJS.of(nbt));
	}
}