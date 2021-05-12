package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ModifiedArmorTier;
import net.minecraft.world.item.ArmorMaterials;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemArmorTierEventJS extends EventJS {
	public void add(String id, Consumer<ModifiedArmorTier> tier) {
		ModifiedArmorTier t = new ModifiedArmorTier(ArmorMaterials.IRON);
		tier.accept(t);
		ItemBuilder.ARMOR_TIERS.put(id, t);
	}
}