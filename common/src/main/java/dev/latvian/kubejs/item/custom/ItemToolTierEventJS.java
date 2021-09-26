package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ModifiedToolTier;
import net.minecraft.world.item.Tiers;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemToolTierEventJS extends StartupEventJS {
	public void add(String id, Consumer<ModifiedToolTier> tier) {
		ModifiedToolTier t = new ModifiedToolTier(Tiers.IRON);
		tier.accept(t);
		ItemBuilder.TOOL_TIERS.put(id, t);
	}
}