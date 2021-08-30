package dev.latvian.kubejs.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Direction.class)
public abstract class DirectionMixin {
	@Shadow
	@RemapForJS("getX")
	public abstract int getStepX();

	@Shadow
	@RemapForJS("getY")
	public abstract int getStepY();

	@Shadow
	@RemapForJS("getZ")
	public abstract int getStepZ();

	@Shadow
	@RemapForJS("getOpposite")
	public abstract Direction getOpposite();

	@Shadow
	@RemapForJS("getIndex")
	public abstract int get3DDataValue();

	@Shadow
	@RemapForJS("getHorizontalIndex")
	public abstract int get2DDataValue();

	@Shadow
	@RemapForJS("getYaw")
	public abstract float toYRot();

	public float getPitch() {
		Object o = this;
		return o == Direction.UP ? 180F : o == Direction.DOWN ? 0F : 90F;
	}

	@Shadow
	@RemapForJS("getClockWise")
	public abstract Direction getClockWise();

	@Shadow
	@RemapForJS("getCounterClockWise")
	public abstract Direction getCounterClockWise();
}
