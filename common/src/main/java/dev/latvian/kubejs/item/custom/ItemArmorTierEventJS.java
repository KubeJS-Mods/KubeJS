package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ModifiedArmorTier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemArmorTierEventJS extends EventJS {
	public void add(String id, String parent, Consumer<ModifiedArmorTier> tier) {
		ArmorMaterial material = ItemBuilder.ARMOR_TIERS.getOrDefault(parent, ArmorMaterials.IRON);
		ModifiedArmorTier t = new ModifiedArmorTier(id, material);
		tier.accept(t);
		ItemBuilder.ARMOR_TIERS.put(id, t);
	}

	public void add(String id, Consumer<ModifiedArmorTier> tier) {
		add(id, "iron", tier);
	}
}