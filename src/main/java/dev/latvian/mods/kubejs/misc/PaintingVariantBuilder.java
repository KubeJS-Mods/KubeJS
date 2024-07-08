package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

@ReturnsSelf
public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
	public transient int width;
	public transient int height;
	public transient ResourceLocation assetId;

	public PaintingVariantBuilder(ResourceLocation id) {
		super(id);
		this.width = 1;
		this.height = 1;
		this.assetId = id;
	}

	@Override
	public PaintingVariant createObject() {
		return new PaintingVariant(width, height, assetId);
	}

	public PaintingVariantBuilder size(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public PaintingVariantBuilder assetId(ResourceLocation assetId) {
		this.assetId = assetId;
		return this;
	}
}
