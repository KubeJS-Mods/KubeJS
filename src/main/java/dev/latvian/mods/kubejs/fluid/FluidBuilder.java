package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

@ReturnsSelf
public class FluidBuilder extends BuilderBase<FlowingFluid> {
	public transient ResourceLocation stillTexture;
	public transient ResourceLocation flowingTexture;
	public transient int color = 0xFFFFFFFF;
	public transient int bucketColor = 0xFFFFFFFF;
	public transient String renderType = "solid";

	public transient Supplier<FluidType> fluidType = NeoForgeMod.WATER_TYPE::value;
	public transient int slopeFindDistance = 4;
	public transient int levelDecreasePerBlock = 1;
	public transient float explosionResistance = 1;
	public transient int tickRate = 5;

	public FlowingFluidBuilder flowingFluid;
	public FluidBlockBuilder block;
	public FluidBucketItemBuilder bucketItem;
	private BaseFlowingFluid.Properties properties;

	public FluidBuilder(ResourceLocation i) {
		super(i);
		stillTexture = newID("block/", "_still");
		flowingTexture = newID("block/", "_flow");
		flowingFluid = new FlowingFluidBuilder(this);
		block = new FluidBlockBuilder(this);
		bucketItem = new FluidBucketItemBuilder(this);
	}

	@Override
	public BuilderBase<FlowingFluid> displayName(Component name) {
		if (block != null) {
			block.displayName(name);
		}

		if (bucketItem != null) {
			bucketItem.displayName(Component.literal("").append(name).append(" Bucket"));
		}

		return super.displayName(name);
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.FLUID;
	}

	public BaseFlowingFluid.Properties createProperties() {
		if (properties == null) {
			properties = new BaseFlowingFluid.Properties(fluidType, this, flowingFluid);

			properties.slopeFindDistance(slopeFindDistance);
			properties.levelDecreasePerBlock(levelDecreasePerBlock);
			properties.explosionResistance(explosionResistance);
			properties.tickRate(tickRate);
		}

		return properties;
	}

	@Override
	public FlowingFluid createObject() {
		return new BaseFlowingFluid.Source(createProperties());
	}

	@Override
	public void createAdditionalObjects(AdditionalObjectRegistry registry) {
		registry.add(RegistryInfo.FLUID, flowingFluid);

		if (block != null) {
			registry.add(RegistryInfo.BLOCK, block);
		}

		if (bucketItem != null) {
			registry.add(RegistryInfo.ITEM, bucketItem);
		}
	}

	@Override
	public BuilderBase<FlowingFluid> tag(ResourceLocation[] tag) {
		this.flowingFluid.tag(tag);
		return super.tag(tag);
	}

	public FluidBuilder color(Color c) {
		this.color = bucketColor = c.getArgbJS();
		return this;
	}

	public FluidBuilder bucketColor(Color c) {
		this.bucketColor = c.getArgbJS();
		return this;
	}

	public FluidBuilder builtinTextures() {
		stillTexture(KubeJS.id("fluid/fluid_thin_still"));
		flowingTexture(KubeJS.id("fluid/fluid_thin_flow"));
		return this;
	}

	public FluidBuilder stillTexture(ResourceLocation id) {
		this.stillTexture = id;
		return this;
	}

	public FluidBuilder flowingTexture(ResourceLocation id) {
		this.flowingTexture = id;
		return this;
	}

	public FluidBuilder renderType(String l) {
		this.renderType = l;
		return this;
	}

	public FluidBuilder translucent() {
		return renderType("translucent");
	}

	public FluidBuilder thickTexture(Color color) {
		return stillTexture(KubeJS.id("block/thick_fluid_still")).flowingTexture(KubeJS.id("block/thick_fluid_flow")).color(color);
	}

	public FluidBuilder thinTexture(Color color) {
		return stillTexture(KubeJS.id("block/thin_fluid_still")).flowingTexture(KubeJS.id("block/thin_fluid_flow")).color(color);
	}

	public FluidBuilder slopeFindDistance(int slopeFindDistance) {
		this.slopeFindDistance = slopeFindDistance;
		return this;
	}

	public FluidBuilder levelDecreasePerBlock(int levelDecreasePerBlock) {
		this.levelDecreasePerBlock = levelDecreasePerBlock;
		return this;
	}

	public FluidBuilder explosionResistance(float explosionResistance) {
		this.explosionResistance = explosionResistance;
		return this;
	}

	public FluidBuilder tickRate(int tickRate) {
		this.tickRate = tickRate;
		return this;
	}

	public FluidBuilder noBucket() {
		this.bucketItem = null;
		return this;
	}

	public FluidBuilder noBlock() {
		this.block = null;
		return this;
	}
}