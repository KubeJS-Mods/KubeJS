package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.BlockKJS;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements BlockKJS
{
	@Override
	@Accessor("material")
	public abstract void setMaterialKJS(Material v);

	@Override
	@Accessor("hasCollision")
	public abstract void setHasCollisionKJS(boolean v);

	@Override
	@Accessor("explosionResistance")
	public abstract void setExplosionResistanceKJS(float v);

	@Override
	@Accessor("isRandomlyTicking")
	public abstract void setIsRandomlyTickingKJS(boolean v);

	@Override
	@Accessor("soundType")
	public abstract void setSoundTypeKJS(SoundType v);

	@Override
	@Accessor("friction")
	public abstract void setFrictionKJS(float v);

	@Override
	@Accessor("speedFactor")
	public abstract void setSpeedFactorKJS(float v);

	@Override
	@Accessor("jumpFactor")
	public abstract void setJumpFactorKJS(float v);
}
