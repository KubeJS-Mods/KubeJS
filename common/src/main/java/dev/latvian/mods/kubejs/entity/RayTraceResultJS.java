package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RayTraceResultJS {
	public final Entity fromEntity;
	public final HitResult.Type type;
	public final double distance;
	public Vec3 hit = null;

	public BlockContainerJS block = null;
	public Direction facing = null;

	public Entity entity = null;

	public RayTraceResultJS(Entity from, @Nullable HitResult result, double d) {
		fromEntity = from;
		distance = d;
		type = result == null ? HitResult.Type.MISS : result.getType();

		if (result instanceof BlockHitResult b && result.getType() == HitResult.Type.BLOCK) {
			hit = result.getLocation();
			block = new BlockContainerJS(from.level, b.getBlockPos());
			facing = b.getDirection();
		} else if (result instanceof EntityHitResult e && result.getType() == HitResult.Type.ENTITY) {
			hit = result.getLocation();
			entity = e.getEntity();
		}
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
