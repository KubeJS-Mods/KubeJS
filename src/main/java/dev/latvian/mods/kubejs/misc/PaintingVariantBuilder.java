package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.Objects;

@ReturnsSelf
public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
	public transient PaintingVariant paintingVariant;

	public PaintingVariantBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.PAINTING_VARIANT;
	}

	@Override
	public PaintingVariant createObject() {
		return Objects.requireNonNull(paintingVariant);
	}

	public PaintingVariantBuilder painting(int width, int height, ResourceLocation texture) {
		this.paintingVariant = new PaintingVariant(width, height, texture);
		return this;
	}
}
