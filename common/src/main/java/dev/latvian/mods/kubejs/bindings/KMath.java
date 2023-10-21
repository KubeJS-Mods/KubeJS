package dev.latvian.mods.kubejs.bindings;

import net.minecraft.core.BlockPos;

public class KMath {
	public static final double E = 2.7182818284590452354;
	public static final double PI = 3.14159265358979323846;
	public static final double DEGREES_TO_RADIANS = 0.017453292519943295;
	public static final double RADIANS_TO_DEGREES = 57.29577951308232;

	public static BlockPos block(double x, double y, double z) {
		return new BlockPos(x, y, z);
	}

	public static double rad(double value) {
		return value * DEGREES_TO_RADIANS;
	}

	public static double deg(double value) {
		return value * RADIANS_TO_DEGREES;
	}

	public static long floor(double value) {
		long i = (long) value;
		return value < i ? i - 1L : i;
	}

	public static long ceil(double value) {
		long i = (long) value;
		return value > i ? i + 1L : i;
	}

	public static double clamp(double value, double min, double max) {
		return value < min ? min : Math.min(value, max);
	}

	public static double lerp(double value, double min, double max) {
		return min + value * (max - min);
	}

	public static double lerp(double value, double min0, double max0, double min1, double max1) {
		return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
	}

	public static double clampedLerp(double value, double min, double max) {
		return value < 0 ? min : value > 1.0 ? max : lerp(value, min, max);
	}

	public static double wrapDegrees(double d) {
		double e = d % 360.0;
		if (e >= 180.0) {
			e -= 360.0;
		}

		if (e < -180.0) {
			e += 360.0;
		}

		return e;
	}

	public static double degreesDifference(double current, double target) {
		return wrapDegrees(target - current);
	}

	public static double rotateIfNecessary(double current, double target, double max) {
		double i = degreesDifference(current, target);
		double j = clamp(i, -max, max);
		return target - j;
	}

	public static double approach(double current, double target, double speed) {
		speed = Math.abs(speed);
		return current < target ? clamp(current + speed, current, target) : clamp(current - speed, target, current);
	}

	public static double approachDegrees(double current, double target, double speed) {
		double i = degreesDifference(current, target);
		return approach(current, current + i, speed);
	}

	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
}
