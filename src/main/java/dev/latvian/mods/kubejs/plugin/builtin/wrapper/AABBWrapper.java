package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface AABBWrapper {
	AABB EMPTY = new AABB(0D, 0D, 0D, 0D, 0D, 0D);
	AABB CUBE = new AABB(0D, 0D, 0D, 1D, 1D, 1D);

	static AABB of(double x0, double y0, double z0, double x1, double y1, double z1) {
		return new AABB(x0, y0, z0, x1, y1, z1);
	}

	static AABB ofBlocks(BlockPos pos1, BlockPos pos2) {
		return of(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX() + 1D, pos2.getY() + 1D, pos2.getZ() + 1D);
	}

	static AABB ofBlock(BlockPos pos) {
		return ofBlocks(pos, pos);
	}

	static AABB ofSize(double x, double y, double z) {
		return ofSize(Vec3.ZERO, x, y, z);
	}

	static AABB ofSize(Vec3 vec3, double x, double y, double z) {
		return AABB.ofSize(vec3, x, y, z);
	}

	static AABB wrap(Object o) {
		return switch (o) {
			case AABB aabb -> aabb;
			case BlockPos blockPos -> ofBlock(blockPos);
			case double[] d -> switch (d.length) {
				case 3 -> ofSize(d[0], d[1], d[2]);
				case 6 -> of(d[0], d[1], d[2], d[3], d[4], d[5]);
				default -> EMPTY;
			};
			case null, default -> EMPTY;
		};
	}
}