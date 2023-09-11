package dev.latvian.mods.kubejs.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum RotationAxis {
	XN(f -> new Quaternionf().rotationX(-f), new Vector3f(-1F, 0F, 0F)),
	XP(f -> new Quaternionf().rotationX(f), new Vector3f(1F, 0F, 0F)),
	YN(f -> new Quaternionf().rotationY(-f), new Vector3f(0F, -1F, 0F)),
	YP(f -> new Quaternionf().rotationY(f), new Vector3f(0F, 1F, 0F)),
	ZN(f -> new Quaternionf().rotationZ(-f), new Vector3f(0F, 0F, -1F)),
	ZP(f -> new Quaternionf().rotationZ(f), new Vector3f(0F, 0F, 1F));

	private interface Func {
		Quaternionf rotation(float f);
	}

	private final Func func;
	public final Vector3f vec;

	RotationAxis(Func func, Vector3f vec) {
		this.func = func;
		this.vec = vec;
	}

	public Quaternionf rad(float f) {
		return func.rotation(f);
	}

	public Quaternionf deg(float f) {
		return func.rotation(f * 0.017453292519943295F);
	}
}
