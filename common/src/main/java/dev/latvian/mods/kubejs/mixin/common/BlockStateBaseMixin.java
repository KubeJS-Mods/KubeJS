package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.BlockStateKJS;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin implements BlockStateKJS {
	@Override
	@Accessor("material")
	@Mutable
	public abstract void setMaterialKJS(Material v);

	@Override
	@Accessor("destroySpeed")
	@Mutable
	public abstract void setDestroySpeedKJS(float v);

	@Override
	@Accessor("requiresCorrectToolForDrops")
	@Mutable
	public abstract void setRequiresToolKJS(boolean v);

	@Override
	@Accessor("lightEmission")
	@Mutable
	public abstract void setLightEmissionKJS(int v);
}
