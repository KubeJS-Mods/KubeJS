package dev.latvian.mods.kubejs.block.callbacks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class EntityBounceCallbackJS extends EntityStepOnBlockCallbackJS {

	private boolean hasChangedVelocity;

	public 	EntityBounceCallbackJS(BlockGetter blockGetter, Entity entity) {
		super(entity.level, entity, entity.getOnPos(), blockGetter.getBlockState(entity.getOnPos()));
		this.hasChangedVelocity = false;
	}

	public boolean isSuppressingBounce() {
		return entity.isSuppressingBounce();
	}

	public void bounce(float strength) {
		Vec3 deltaMovement = entity.getDeltaMovement();
		if (!entity.isSuppressingBounce() && deltaMovement.y < 0.0) {
			entity.setDeltaMovement(deltaMovement.x, -deltaMovement.y * strength, deltaMovement.z);
			hasChangedVelocity = true;
		}
	}

	public Vec3 getVelocity() {
		return entity.getDeltaMovement();
	}

	public void setVelocity(Vec3 vec) {
		entity.setDeltaMovement(vec);
		hasChangedVelocity = true;
	}

	public void setVelocity(float x, float y, float z) {
		setVelocity(new Vec3(x, y, z));
	}

	public boolean hasChangedVelocity() {
		return hasChangedVelocity;
	}
}
