package dev.latvian.mods.kubejs.fluid.forge;

import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

public class KubeJSFluidHelperImpl {
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		if (source) {
			return new ForgeFlowingFluid.Source(createProperties(builder));
		} else {
			return new ForgeFlowingFluid.Flowing(createProperties(builder));
		}
	}

	public static ForgeFlowingFluid.Properties createProperties(FluidBuilder fluidBuilder) {
		if (fluidBuilder.extraPlatformInfo != null) {
			return (ForgeFlowingFluid.Properties) fluidBuilder.extraPlatformInfo;
		}

		var builder = FluidAttributes.builder(fluidBuilder.stillTexture, fluidBuilder.flowingTexture)
				.translationKey("fluid." + fluidBuilder.id.getNamespace() + "." + fluidBuilder.id.getPath())
				.color(fluidBuilder.color)
				.rarity(fluidBuilder.rarity)
				.density(fluidBuilder.density)
				.viscosity(fluidBuilder.viscosity)
				.luminosity(fluidBuilder.luminosity)
				.temperature(fluidBuilder.temperature);

		if (fluidBuilder.isGaseous) {
			builder.gaseous();
		}

		var properties = new ForgeFlowingFluid.Properties(fluidBuilder, fluidBuilder.flowingFluid, builder).bucket(fluidBuilder.bucketItem).block(() -> (LiquidBlock) fluidBuilder.block.get());
		fluidBuilder.extraPlatformInfo = properties;
		return properties;
	}

	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		return new BucketItemJS(builder, itemBuilder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder fluidBuilder;

		public BucketItemJS(FluidBuilder b, FluidBucketItemBuilder itemBuilder) {
			super(b, itemBuilder.createItemProperties());
			fluidBuilder = b;
		}

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
			return new FluidBucketWrapper(stack);
		}
	}

	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		return new LiquidBlock(() -> (FlowingFluid) builder.get(), properties);
	}

	public static Fluid getContainedFluid(Item item) {
		return item instanceof BucketItem bucketItem ? bucketItem.getFluid() : null;
	}
}
