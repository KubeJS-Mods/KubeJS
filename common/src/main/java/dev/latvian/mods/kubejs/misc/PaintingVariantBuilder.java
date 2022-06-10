package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
	public transient int width;
	public transient int height;

	public PaintingVariantBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryObjectBuilderTypes<PaintingVariant> getRegistryType() {
		return RegistryObjectBuilderTypes.PAINTING_VARIANT;
	}

	@Override
	public PaintingVariant createObject() {
		return new PaintingVariant(width, height);
	}

	public PaintingVariantBuilder width(int width) {
		this.width = width;
		return this;
	}

	public PaintingVariantBuilder height(int height) {
		this.height = height;
		return this;
	}
}
