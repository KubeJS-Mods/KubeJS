package dev.latvian.mods.kubejs.block.callbacks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class EntityBounceCallbackJS extends EntityStepOnBlockCallbackJS {

	public EntityBounceCallbackJS(BlockGetter blockGetter, Entity entity) {
		super(entity.level, entity, entity.getOnPos(), blockGetter.getBlockState(entity.getOnPos()));
	}

	public void bounce(float height) {
		bounce(0, height,0);
	}

	public void bounce(float x,float y,float z) {
		if (!entity.isSuppressingBounce()) {
			Vec3 deltaMovement = entity.getDeltaMovement();
			if (deltaMovement.y < 0.0) {
				double d = entity instanceof LivingEntity ? 1.0 : 0.8;
				entity.setDeltaMovement(deltaMovement.x * x, -deltaMovement.y * d, deltaMovement.z * z);
			}
		}
//		entity.hurtMarked = true;
	}
}
