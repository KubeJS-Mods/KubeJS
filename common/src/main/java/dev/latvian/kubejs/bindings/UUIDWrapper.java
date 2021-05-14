package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.UUIDUtilsJS;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class UUIDWrapper {
	public static String toString(UUID id) {
		return UUIDUtilsJS.toString(id);
	}

	@Nullable
	public static UUID fromString(Object string) {
		return UUIDUtilsJS.fromString(String.valueOf(string));
	}
}