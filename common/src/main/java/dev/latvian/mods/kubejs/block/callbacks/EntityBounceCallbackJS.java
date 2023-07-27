package dev.latvian.mods.kubejs.block.callbacks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class EntityBounceCallbackJS extends EntityStepOnBlockCallbackJS {

	public EntityBounceCallbackJS(BlockGetter blockGetter, Entity entity) {
		super(entity.level, entity, entity.getOnPos(), blockGetter.getBlockState(entity.getOnPos()));
	}

	public boolean isSuppressingBounce() {
		return entity.isSuppressingBounce();
	}

	public void bounce(float height) {
		bounce(0, height,0, 0.1f);
	}

	public void bounce(float x,float y, float z, float minHeight) {
		Vec3 deltaMovement = entity.getDeltaMovement();
		if (!entity.isSuppressingBounce() && deltaMovement.y < -minHeight) {
			entity.setDeltaMovement(deltaMovement.x * x, -deltaMovement.y * y, deltaMovement.z * z);
		}
//		entity.hurtMarked = true;
	}
}
