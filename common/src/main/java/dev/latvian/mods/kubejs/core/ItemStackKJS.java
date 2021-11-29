package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemStackJS;

public interface ItemStackKJS extends AsKJS {
	@Override
	default Object asKJS() {
		return ItemStackJS.of(this);
	}

	void removeTagKJS();
}
