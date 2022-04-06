package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;

public class MotiveBuilder extends BuilderBase<Motive> {
	public transient int width;
	public transient int height;

	public MotiveBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public final RegistryObjectBuilderTypes<Motive> getRegistryType() {
		return RegistryObjectBuilderTypes.MOTIVE;
	}

	@Override
	public Motive createObject() {
		return new Motive(width, height);
	}

	public MotiveBuilder width(int width) {
		this.width = width;
		return this;
	}

	public MotiveBuilder height(int height) {
		this.height = height;
		return this;
	}
}
