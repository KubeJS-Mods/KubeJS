package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CustomBuilderObject extends BuilderBase {
	private final Supplier<Object> object;

	public CustomBuilderObject(ResourceLocation i, Supplier<Object> object) {
		super(i);
		this.object = object;
	}

	@Override
	public Object createObject() {
		return object.get();
	}
}