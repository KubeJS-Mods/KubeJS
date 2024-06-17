package dev.latvian.mods.kubejs.util;

// TODO: Remove
public enum JSObjectType {
	ANY,
	MAP,
	LIST;

	public boolean checkMap() {
		return this == ANY || this == MAP;
	}

	public boolean checkList() {
		return this == ANY || this == LIST;
	}
}