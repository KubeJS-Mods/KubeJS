package dev.latvian.mods.kubejs.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public final class KubeJSModelPropertyRegistry {
	private static final Map<Identifier, ItemModelPropertyTest> CONDITIONAL = new Object2ObjectOpenHashMap<>();

	public static void putConditional(Identifier id, ItemModelPropertyTest cb) {
		CONDITIONAL.put(id, cb);
	}

	@Nullable
	public static ItemModelPropertyTest getConditional(Identifier id) {
		return CONDITIONAL.get(id);
	}
}
