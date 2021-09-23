package dev.latvian.kubejs.item.custom.fabric;

import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.custom.BasicItemJS;

public class BasicItemUtilImpl {
	public static BasicItemJS createBasicItem(ItemBuilder p) {
		return new FabricBasicItemJS(p);
	}
}
