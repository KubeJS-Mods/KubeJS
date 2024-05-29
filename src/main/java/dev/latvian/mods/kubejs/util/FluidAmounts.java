package dev.latvian.mods.kubejs.util;

public interface FluidAmounts {
	long BUCKET = 1000;
	long MILLIBUCKET = BUCKET / 1000;
	long B = BUCKET, MB = MILLIBUCKET;
	long INGOT = 90;
	long NUGGET = INGOT / 9;
	long METAL_BLOCK = INGOT * 9;
	long BOTTLE = 250;
}
