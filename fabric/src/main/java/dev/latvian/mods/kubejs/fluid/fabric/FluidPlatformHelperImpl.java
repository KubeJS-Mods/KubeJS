package dev.latvian.mods.kubejs.fluid.fabric;

import dev.latvian.mods.kubejs.fluid.FluidBucketItemBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class FluidPlatformHelperImpl {
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new RuntimeException("Fluid registry isn't implemented on Fabric yet!");
	}

	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		return new LiquidBlock((FlowingFluid) builder.get(), properties) {
		};
	}

	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		return new BucketItemJS(builder, itemBuilder);
	}

	public static class BucketItemJS extends BucketItem {
		public final FluidBuilder fluidBuilder;

		public BucketItemJS(FluidBuilder b, FluidBucketItemBuilder itemBuilder) {
			super(b.get(), itemBuilder.createItemProperties());
			fluidBuilder = b;
		}
	}

	public static Fluid getContainedFluid(Item item) {
		return null;
	}
}
