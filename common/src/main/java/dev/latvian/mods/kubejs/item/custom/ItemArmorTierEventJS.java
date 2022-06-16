package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableArmorTier;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ItemArmorTierEventJS extends StartupEventJS {
	public void add(String id, String parent, Consumer<MutableArmorTier> tier) {
		var material = ItemBuilder.ofArmorMaterial(parent);
		var fullId = KubeJS.appendModId(id);
		var t = new MutableArmorTier(fullId, material);
		tier.accept(t);
		ItemBuilder.ARMOR_TIERS.put(fullId, t);
	}

	public void add(String id, Consumer<MutableArmorTier> tier) {
		add(id, "iron", tier);
	}
}