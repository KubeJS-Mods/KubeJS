package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

@ReturnsSelf
public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
	public transient int width;
	public transient int height;

	public PaintingVariantBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.PAINTING_VARIANT;
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
