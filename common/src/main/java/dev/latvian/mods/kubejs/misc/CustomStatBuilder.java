package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;

public class CustomStatBuilder extends BuilderBase<ResourceLocation> {
	public CustomStatBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryObjectBuilderTypes<ResourceLocation> getRegistryType() {
		return RegistryObjectBuilderTypes.CUSTOM_STAT;
	}

	@Override
	public ResourceLocation createObject() {
		return id;
	}
}
