package dev.latvian.kubejs.core;

import dev.latvian.kubejs.item.ItemStackJS;

public interface ItemStackKJS extends AsKJS {
	@Override
	default Object asKJS() {
		return ItemStackJS.of(this);
	}

	void removeTagKJS();
}
