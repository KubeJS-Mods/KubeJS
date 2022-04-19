package dev.latvian.mods.kubejs.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.core.BucketItemKJS;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * @author LatvianModder
 */
public class FluidPlatformHelper {
	@ExpectPlatform
	public static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static BucketItem buildBucket(FluidBuilder builder, FluidBucketItemBuilder itemBuilder) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Fluid getContainedFluid(Item item) {
		throw new AssertionError();
	}

	public static Fluid getActualContainedFluid(Item item) {
		Fluid f = getContainedFluid(item);

		if (f != null) {
			return f;
		} else if (item instanceof BucketItemKJS bucket && bucket.getFluidKJS() != null) {
			return bucket.getFluidKJS();
		}

		return Fluids.EMPTY;
	}
}