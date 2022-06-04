package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemStackJS;

public interface ItemStackKJS extends AsKJS<ItemStackJS> {
	@Override
	default ItemStackJS asKJS() {
		return ItemStackJS.of(this);
	}

	void removeTagKJS();
}
