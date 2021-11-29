package dev.latvian.mods.kubejs.item.custom.fabric;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;

public class BasicItemUtilImpl {
	public static BasicItemJS createBasicItem(ItemBuilder p) {
		return new FabricBasicItemJS(p);
	}
}
