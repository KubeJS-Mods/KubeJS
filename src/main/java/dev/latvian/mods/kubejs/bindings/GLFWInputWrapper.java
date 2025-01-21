package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public interface GLFWInputWrapper {
	Lazy<Map<String, Integer>> MAP = Lazy.of(() -> {
		var map = new HashMap<String, Integer>();

		try {
			for (var field : GLFW.class.getDeclaredFields()) {
				int mod = field.getModifiers();

				if (field.getType() == int.class && Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
					var n = field.getName();

					if (n.startsWith("GLFW_KEY_") || n.startsWith("GLFW_MOUSE_") || n.startsWith("GLFW_GAMEPAD_") || n.startsWith("GLFW_CURSOR_")) {
						map.put(n.substring(5), field.getInt(null));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Map.copyOf(map);
	});

	static int get(String name) {
		return MAP.get().getOrDefault(name, -1);
	}
}
