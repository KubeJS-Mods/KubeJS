package dev.latvian.mods.kubejs.recipe.component;

public enum ComponentRole {
	INPUT,
	OUTPUT,
	OTHER;

	public boolean isInput() {
		return this == INPUT;
	}

	public boolean isOutput() {
		return this == OUTPUT;
	}

	public boolean isOther() {
		return this == OTHER;
	}
}
