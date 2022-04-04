package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.util.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;

/**
 * @author LatvianModder
 */
public class FluidBuilder extends BuilderBase<Fluid> {
	public String stillTexture;
	public String flowingTexture;
	public int color = 0xFFFFFFFF;
	public int bucketColor = 0xFFFFFFFF;
	public int luminosity = 0;
	public int density = 1000;
	public int temperature = 300;
	public int viscosity = 1000;
	public boolean isGaseous;
	public Rarity rarity = Rarity.COMMON;
	public Object extraPlatformInfo;

	public FlowingFluidBuilder flowingFluid;
	public FluidBlockBuilder block;
	public FluidBucketItemBuilder bucketItem;

	public FluidBuilder(ResourceLocation i) {
		super(i);
		textureStill(KubeJS.id("fluid/fluid_thin"));
		textureFlowing(KubeJS.id("fluid/fluid_thin_flow"));
		flowingFluid = new FlowingFluidBuilder(this);
		block = new FluidBlockBuilder(this);
		bucketItem = new FluidBucketItemBuilder(this);
		bucketItem.displayName(displayName + " Bucket");
	}

	@Override
	public BuilderBase<Fluid> displayName(String name) {
		bucketItem.displayName(name + " Bucket");
		return super.displayName(name);
	}

	@Override
	public final RegistryObjectBuilderTypes<Fluid> getRegistryType() {
		return RegistryObjectBuilderTypes.FLUID;
	}

	@Override
	public Fluid createObject() {
		return KubeJSFluidHelper.buildFluid(true, this);
	}

	@Override
	public void createAdditionalObjects() {
		RegistryObjectBuilderTypes.FLUID.addBuilder(flowingFluid);
		RegistryObjectBuilderTypes.BLOCK.addBuilder(block);
		RegistryObjectBuilderTypes.ITEM.addBuilder(bucketItem);
	}

	public FluidBuilder color(int c) {
		color = c;

		if ((color & 0xFFFFFF) == color) {
			color |= 0xFF000000;
		}

		return bucketColor(color);
	}

	public FluidBuilder bucketColor(int c) {
		bucketColor = c;

		if ((bucketColor & 0xFFFFFF) == bucketColor) {
			bucketColor |= 0xFF000000;
		}

		return this;
	}

	public FluidBuilder textureStill(ResourceLocation id) {
		stillTexture = id.toString();
		return this;
	}

	public FluidBuilder textureFlowing(ResourceLocation id) {
		flowingTexture = id.toString();
		return this;
	}

	public FluidBuilder textureThick(int color) {
		return textureStill(KubeJS.id("fluid/fluid_thick")).textureFlowing(KubeJS.id("fluid/fluid_thick_flow")).color(color);
	}

	public FluidBuilder textureThin(int color) {
		return textureStill(KubeJS.id("fluid/fluid_thin")).textureFlowing(KubeJS.id("fluid/fluid_thin_flow")).color(color);
	}

	public FluidBuilder luminosity(int luminosity) {
		this.luminosity = luminosity;
		return this;
	}

	public FluidBuilder density(int density) {
		this.density = density;
		return this;
	}

	public FluidBuilder temperature(int temperature) {
		this.temperature = temperature;
		return this;
	}

	public FluidBuilder viscosity(int viscosity) {
		this.viscosity = viscosity;
		return this;
	}

	public FluidBuilder gaseous() {
		isGaseous = true;
		return this;
	}

	public FluidBuilder rarity(Rarity rarity) {
		this.rarity = rarity;
		return this;
	}
}