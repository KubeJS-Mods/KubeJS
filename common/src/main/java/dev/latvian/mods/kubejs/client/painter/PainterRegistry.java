package dev.latvian.mods.kubejs.client.painter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PainterRegistry {
	private static final Supplier<PainterObject> NULL_PAINTER_OBJECT = () -> null;
	private static final Map<String, Supplier<PainterObject>> FACTORIES = new HashMap<>();

	public static void register(String name, Supplier<PainterObject> factory) {
		FACTORIES.put(name, factory);
	}

	@Nullable
	public static PainterObject make(String name) {
		return FACTORIES.getOrDefault(name, NULL_PAINTER_OBJECT).get();
	}

}
