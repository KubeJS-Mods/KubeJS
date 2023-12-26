package dev.latvian.mods.kubejs.util;

import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.latvian.mods.kubejs.helpers.MiscHelper;

public interface FluidAmounts {
	long BUCKET = FluidStackHooks.bucketAmount();

	long MILLIBUCKET = BUCKET / 1000;

	long B = BUCKET, MB = MILLIBUCKET;

	long INGOT = MiscHelper.get().ingotFluidAmount();

	long NUGGET = INGOT / 9;

	long METAL_BLOCK = INGOT * 9;

	long BOTTLE = MiscHelper.get().bottleFluidAmount();
}
