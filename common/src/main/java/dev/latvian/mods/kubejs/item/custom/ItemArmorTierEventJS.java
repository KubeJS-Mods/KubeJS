package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableArmorTier;
import net.minecraft.world.item.ArmorMaterials;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemArmorTierEventJS extends StartupEventJS {
	public void add(String id, String parent, Consumer<MutableArmorTier> tier) {
		var material = ItemBuilder.ARMOR_TIERS.getOrDefault(parent, ArmorMaterials.IRON);
		var t = new MutableArmorTier(id, material);
		tier.accept(t);
		ItemBuilder.ARMOR_TIERS.put(id, t);
	}

	public void add(String id, Consumer<MutableArmorTier> tier) {
		add(id, "iron", tier);
	}
}