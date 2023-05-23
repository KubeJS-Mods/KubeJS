package dev.latvian.mods.kubejs.util;

import java.util.HashMap;
import java.util.Map;

public class CustomModNames {
	private static final Map<String, String> MAP = new HashMap<>();

	public static void set(String modId, String name) {
		if (name == null || name.isBlank()) {
			MAP.remove(modId);
		} else {
			MAP.put(modId, name);
		}
	}

	public static String get(String modId) {
		return MAP.getOrDefault(modId, "");
	}
}
