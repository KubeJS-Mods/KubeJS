package dev.latvian.mods.kubejs.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public enum RotationAxis {
	XN(Vector3f.XN::rotation, new Vector3f(-1F, 0F, 0F)),
	XP(Vector3f.XP::rotation, new Vector3f(1F, 0F, 0F)),
	YN(Vector3f.YN::rotation, new Vector3f(0F, -1F, 0F)),
	YP(Vector3f.YP::rotation, new Vector3f(0F, 1F, 0F)),
	ZN(Vector3f.ZN::rotation, new Vector3f(0F, 0F, -1F)),
	ZP(Vector3f.ZP::rotation, new Vector3f(0F, 0F, 1F));

	private interface Func {
		Quaternion rotation(float f);
	}

	private final Func func;
	public final Vector3f vec;

	RotationAxis(Func func, Vector3f vec) {
		this.func = func;
		this.vec = vec;
	}

	public Quaternion rad(float f) {
		return func.rotation(f);
	}

	public Quaternion deg(float f) {
		return func.rotation(f * 0.017453292519943295F);
	}
}
