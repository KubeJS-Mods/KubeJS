package dev.latvian.mods.kubejs.bindings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface KMath {
	double E = 2.7182818284590452354;
	double PI = 3.14159265358979323846;
	double DEGREES_TO_RADIANS = 0.017453292519943295;
	double RADIANS_TO_DEGREES = 57.29577951308232;

	static BlockPos block(double x, double y, double z) {
		return BlockPos.containing(x, y, z);
	}

	// why
	static Vec3 v3(double x, double y, double z) {
		return new Vec3(x, y, z);
	}

	static Vector3d v3d(double x, double y, double z) {
		return new Vector3d(x, y, z);
	}

	static Vector3f v3f(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}

	static Vector4f v4f(float x, float y, float z, float w) {
		return new Vector4f(x, y, z, w);
	}

	static Matrix3f m3f() {
		return new Matrix3f();
	}

	static Matrix4f m4f() {
		return new Matrix4f();
	}

	static PoseStack poseStack() {
		return new PoseStack();
	}

	static Quaternionf quaternion(float x, float y, float z, float w) {
		return new Quaternionf(x, y, z, w);
	}

	static double rad(double value) {
		return value * DEGREES_TO_RADIANS;
	}

	static double deg(double value) {
		return value * RADIANS_TO_DEGREES;
	}

	static long floor(double value) {
		long i = (long) value;
		return value < i ? i - 1L : i;
	}

	static long ceil(double value) {
		long i = (long) value;
		return value > i ? i + 1L : i;
	}

	static double clamp(double value, double min, double max) {
		return value < min ? min : Math.min(value, max);
	}

	static double lerp(double value, double min, double max) {
		return min + value * (max - min);
	}

	static double map(double value, double min0, double max0, double min1, double max1) {
		return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
	}

	static double clampedLerp(double value, double min, double max) {
		return value < 0 ? min : value > 1.0 ? max : lerp(value, min, max);
	}

	static double wrapDegrees(double d) {
		double e = d % 360.0;
		if (e >= 180.0) {
			e -= 360.0;
		}

		if (e < -180.0) {
			e += 360.0;
		}

		return e;
	}

	static double degreesDifference(double current, double target) {
		return wrapDegrees(target - current);
	}

	static double rotateIfNecessary(double current, double target, double max) {
		double i = degreesDifference(current, target);
		double j = clamp(i, -max, max);
		return target - j;
	}

	static double approach(double current, double target, double speed) {
		speed = Math.abs(speed);
		return current < target ? clamp(current + speed, current, target) : clamp(current - speed, target, current);
	}

	static double approachDegrees(double current, double target, double speed) {
		double i = degreesDifference(current, target);
		return approach(current, current + i, speed);
	}

	static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
}
