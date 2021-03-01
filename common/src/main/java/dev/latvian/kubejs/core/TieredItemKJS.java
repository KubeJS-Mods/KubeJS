package dev.latvian.kubejs.core;

import net.minecraft.world.item.Tier;

/**
 * @author LatvianModder
 */
public interface TieredItemKJS {
	Tier getTierKJS();

	void setTierKJS(Tier tier);
}
