package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.BlockStateKJS;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin implements BlockStateKJS
{
	@Override
	@Accessor("material")
	public abstract void setMaterialKJS(Material v);

	@Override
	@Accessor("destroySpeed")
	public abstract void setDestroySpeedKJS(float v);

	@Override
	@Accessor("requiresCorrectToolForDrops")
	public abstract void setRequiresToolKJS(boolean v);

	@Override
	@Accessor("lightEmission")
	public abstract void setLightEmissionKJS(int v);
}
