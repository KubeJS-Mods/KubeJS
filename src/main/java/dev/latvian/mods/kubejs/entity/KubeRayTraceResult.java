package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class KubeRayTraceResult {
	public final Entity fromEntity;
	public final HitResult.Type type;
	public final double distance;
	public final Vec3 hit;
	public final LevelBlock block;
	public final Direction facing;
	public final Entity entity;

	public KubeRayTraceResult(Entity from, @Nullable HitResult result, double d) {
		this.fromEntity = from;
		this.distance = d;
		this.type = result == null ? HitResult.Type.MISS : result.getType();
		this.hit = result == null ? from.getEyePosition() : result.getLocation();

		if (result instanceof BlockHitResult b && result.getType() == HitResult.Type.BLOCK) {
			this.block = from.level().kjs$getBlock(b.getBlockPos());
			this.facing = b.getDirection();
			this.entity = null;
		} else if (result instanceof EntityHitResult e && result.getType() == HitResult.Type.ENTITY) {
			this.block = null;
			this.facing = null;
			this.entity = e.getEntity();
		} else {
			this.block = null;
			this.facing = null;
			this.entity = null;
		}
	}

	public KubeRayTraceResult(Entity from, HitResult result) {
		this(from, result, result.getLocation().distanceTo(from.getEyePosition()));
	}

	public double getHitX() {
		return hit == null ? Double.NaN : hit.x;
	}

	public double getHitY() {
		return hit == null ? Double.NaN : hit.y;
	}

	public double getHitZ() {
		return hit == null ? Double.NaN : hit.z;
	}
}
