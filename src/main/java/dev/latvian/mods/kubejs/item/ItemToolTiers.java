package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemToolTiers {
	public static final Lazy<Map<String, Tier>> ALL = Lazy.of(() -> {
		var map = new HashMap<String, Tier>();

		for (var tier : Tiers.values()) {
			map.put(tier.toString().toLowerCase(Locale.ROOT), tier);
		}

		ItemEvents.TOOL_TIER_REGISTRY.post(ScriptType.STARTUP, new ItemToolTierRegistryKubeEvent(map));
		return map;
	});

	public static Tier wrap(Object o) {
		if (o instanceof Tier tier) {
			return tier;
		}

		var asString = String.valueOf(o);
		var toolTier = ALL.get().get(asString);

		if (toolTier != null) {
			return toolTier;
		}

		return ALL.get().getOrDefault(ID.kjsString(asString), Tiers.IRON);
	}
}
