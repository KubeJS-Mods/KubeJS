package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.world.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class RayTraceResultJS {
	public final EntityJS fromEntity;
	public final String type;
	public final double distance;
	public double hitX = Double.NaN;
	public double hitY = Double.NaN;
	public double hitZ = Double.NaN;

	public BlockContainerJS block = null;
	public Direction facing = null;

	public EntityJS entity = null;

	public RayTraceResultJS(EntityJS from, @Nullable HitResult result, double d) {
		fromEntity = from;
		distance = d;

		if (result instanceof BlockHitResult && result.getType() == HitResult.Type.BLOCK) {
			type = "block";
			hitX = result.getLocation().x;
			hitY = result.getLocation().y;
			hitZ = result.getLocation().z;

			block = new BlockContainerJS(from.minecraftEntity.level, ((BlockHitResult) result).getBlockPos());
			facing = ((BlockHitResult) result).getDirection();
		} else if (result instanceof EntityHitResult && result.getType() == HitResult.Type.ENTITY) {
			type = "entity";
			hitX = result.getLocation().x;
			hitY = result.getLocation().y;
			hitZ = result.getLocation().z;

			entity = fromEntity.getLevel().getEntity(((EntityHitResult) result).getEntity());
		} else {
			type = "miss";
		}
	}
}
