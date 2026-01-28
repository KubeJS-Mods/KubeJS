package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.Identifier;

public class CustomStatBuilder extends BuilderBase<Identifier> {
	public CustomStatBuilder(Identifier i) {
		super(i);
	}

	@Override
	public Identifier createObject() {
		return id;
	}
}
