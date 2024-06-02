package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum ComponentRole implements StringRepresentable {
	INPUT("input"),
	OUTPUT("output"),
	OTHER("other");

	public static final Codec<ComponentRole> CODEC = StringRepresentable.fromEnum(ComponentRole::values);

	private final String name;

	ComponentRole(String name) {
		this.name = name;
	}

	public boolean isInput() {
		return this == INPUT;
	}

	public boolean isOutput() {
		return this == OUTPUT;
	}

	public boolean isOther() {
		return this == OTHER;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
