package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.item.custom.ItemToolMaterialRegistryKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.world.item.ToolMaterial;

import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;

public class ItemToolMaterials {
	public static final Lazy<Map<String, ToolMaterial>> ALL = Lazy.map(map -> {
		for (var f : ToolMaterial.class.getDeclaredFields()) {
			if (f.getType() == ToolMaterial.class && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
				try {
					map.put(f.getName().toLowerCase(Locale.ROOT), (ToolMaterial) f.get(null));
				} catch (IllegalAccessException ignored) {
				}
			}
		}

		ItemEvents.TOOL_MATERIAL_REGISTRY.post(ScriptType.STARTUP, new ItemToolMaterialRegistryKubeEvent(map));
	});

	public static ToolMaterial wrap(Object o) {
		if (o instanceof ToolMaterial tm) {
			return tm;
		}

		var asString = String.valueOf(o).toLowerCase(Locale.ROOT);

		var toolMaterial = ALL.get().get(asString);
		if (toolMaterial != null) {
			return toolMaterial;
		}

		return ALL.get().getOrDefault(ID.kjsString(asString), ToolMaterial.IRON);
	}
}
