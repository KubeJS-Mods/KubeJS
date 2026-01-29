package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.util.KubeIdentifier;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

import java.util.Optional;

@ReturnsSelf
public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
	public transient int width;
	public transient int height;
	public transient Identifier assetId;
	public transient Optional<Component> title;
	public transient Optional<Component> author;

	public PaintingVariantBuilder(Identifier id) {
		super(id);
		this.width = 1;
		this.height = 1;
		this.assetId = id;
		this.title = Optional.empty();
		this.author = Optional.empty();
	}

	@Override
	public PaintingVariant createObject() {
		return new PaintingVariant(width, height, assetId, title, author);
	}

	public PaintingVariantBuilder size(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public PaintingVariantBuilder assetId(KubeIdentifier assetId) {
		this.assetId = assetId.wrapped();
		return this;
	}

	public PaintingVariantBuilder title(Component v) {
		this.title = Optional.ofNullable(v);
		return this;
	}

	public PaintingVariantBuilder author(Component v) {
		this.author = Optional.ofNullable(v);
		return this;
	}
}
