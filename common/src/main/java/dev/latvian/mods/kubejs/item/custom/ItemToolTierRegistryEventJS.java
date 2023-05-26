package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import net.minecraft.world.item.Tiers;

import java.util.function.Consumer;

public class ItemToolTierRegistryEventJS extends StartupEventJS {
	public void add(String id, Consumer<MutableToolTier> tier) {
		var t = new MutableToolTier(Tiers.IRON);
		tier.accept(t);
		ItemBuilder.TOOL_TIERS.put(id, t);
	}
}