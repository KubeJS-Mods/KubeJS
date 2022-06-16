package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class FluidBuilder extends BuilderBase<FlowingFluid> {
	public transient ResourceLocation stillTexture;
	public transient ResourceLocation flowingTexture;
	public transient int color = 0xFFFFFFFF;
	public transient int bucketColor = 0xFFFFFFFF;
	public transient int luminosity = 0;
	public transient int density = 1000;
	public transient int temperature = 300;
	public transient int viscosity = 1000;
	public transient boolean isGaseous;
	public transient Rarity rarity = Rarity.COMMON;
	public ArchitecturyFluidAttributes attributes;

	public FlowingFluidBuilder flowingFluid;
	public FluidBlockBuilder block;
	public FluidBucketItemBuilder bucketItem;

	public FluidBuilder(ResourceLocation i) {
		super(i);
		stillTexture = newID("block/", "_still");
		flowingTexture = newID("block/", "_flow");
		flowingFluid = new FlowingFluidBuilder(this);
		block = new FluidBlockBuilder(this);
		bucketItem = new FluidBucketItemBuilder(this);
		bucketItem.displayName(displayName + " Bucket");
	}

	@Override
	public BuilderBase<FlowingFluid> displayName(String name) {
		if (block != null) {
			block.displayName(name);
		}
		if (bucketItem != null) {
			bucketItem.displayName(name + " Bucket");
		}
		return super.displayName(name);
	}

	@Override
	public final RegistryObjectBuilderTypes<Fluid> getRegistryType() {
		return RegistryObjectBuilderTypes.FLUID;
	}

	@Override
	public FlowingFluid createObject() {
		return new ArchitecturyFlowingFluid.Source(createAttributes());
	}

	public ArchitecturyFluidAttributes createAttributes() {
		if (this.attributes != null) {
			return this.attributes;
		}

		var attributes = SimpleArchitecturyFluidAttributes.of(this.flowingFluid, this)
				.flowingTexture(flowingTexture)
				.sourceTexture(stillTexture)
				.color(color)
				.rarity(rarity)
				.density(density)
				.viscosity(viscosity)
				.luminosity(luminosity)
				.temperature(temperature)
				.lighterThanAir(isGaseous)
				.bucketItem(() -> Optional.ofNullable(bucketItem).map(Supplier::get))
				.block(() -> Optional.ofNullable(block).map(Supplier::get).map(UtilsJS::cast));

		this.attributes = attributes;
		return attributes;
	}

	@Override
	public void createAdditionalObjects() {
		RegistryObjectBuilderTypes.FLUID.addBuilder(flowingFluid);
		if (block != null) {
			RegistryObjectBuilderTypes.BLOCK.addBuilder(block);
		}
		if (bucketItem != null) {
			RegistryObjectBuilderTypes.ITEM.addBuilder(bucketItem);
		}
	}

	public FluidBuilder color(Color c) {
		color = bucketColor = c.getArgbJS();
		return this;
	}

	public FluidBuilder bucketColor(Color c) {
		bucketColor = c.getArgbJS();
		return this;
	}

	public FluidBuilder builtinTextures() {
		stillTexture(KubeJS.id("fluid/fluid_thin"));
		flowingTexture(KubeJS.id("fluid/fluid_thin_flow"));
		return this;
	}

	public FluidBuilder stillTexture(ResourceLocation id) {
		stillTexture = id;
		return this;
	}

	public FluidBuilder flowingTexture(ResourceLocation id) {
		flowingTexture = id;
		return this;
	}

	public FluidBuilder thickTexture(Color color) {
		return stillTexture(KubeJS.id("block/thick_fluid_still")).flowingTexture(KubeJS.id("block/thick_fluid_flow")).color(color);
	}

	public FluidBuilder thinTexture(Color color) {
		return stillTexture(KubeJS.id("block/thin_fluid_still")).flowingTexture(KubeJS.id("block/thin_fluid_flow")).color(color);
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

	public FluidBuilder noBucket() {
		bucketItem = null;
		return this;
	}

	public FluidBuilder noBlock() {
		block = null;
		return this;
	}
}