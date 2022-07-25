package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class RayTraceResultJS {
	public final Entity fromEntity;
	public final String type;
	public final double distance;
	public double hitX = Double.NaN;
	public double hitY = Double.NaN;
	public double hitZ = Double.NaN;

	public BlockContainerJS block = null;
	public Direction facing = null;

	public Entity entity = null;

	public RayTraceResultJS(Entity from, @Nullable HitResult result, double d) {
		fromEntity = from;
		distance = d;

		if (result instanceof BlockHitResult b && result.getType() == HitResult.Type.BLOCK) {
			type = "block";
			hitX = result.getLocation().x;
			hitY = result.getLocation().y;
			hitZ = result.getLocation().z;

			block = new BlockContainerJS(from.level, b.getBlockPos());
			facing = b.getDirection();
		} else if (result instanceof EntityHitResult e && result.getType() == HitResult.Type.ENTITY) {
			type = "entity";
			hitX = result.getLocation().x;
			hitY = result.getLocation().y;
			hitZ = result.getLocation().z;

			entity = e.getEntity();
		} else {
			type = "miss";
		}
	}
}
