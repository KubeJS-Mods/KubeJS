package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.util.JsonUtilsJS;
import dev.latvian.mods.kubejs.util.MapJS;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author LatvianModder
 */
public class JsonIOWrapper {
	@Nullable
	public static MapJS read(File file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	public static void write(File file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}

	@Nullable
	public static MapJS read(String file) throws IOException {
		return JsonUtilsJS.read(file);
	}

	public static void write(String file, Object json) throws IOException {
		JsonUtilsJS.write(file, MapJS.of(json));
	}
}