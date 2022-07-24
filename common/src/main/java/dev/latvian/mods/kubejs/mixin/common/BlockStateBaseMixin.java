package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.BlockStateKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockStateBaseMixin implements BlockStateKJS {
	@Override
	@Accessor("material")
	@Mutable
	public abstract void kjs$setMaterial(Material v);

	@Override
	@Accessor("destroySpeed")
	@Mutable
	public abstract void kjs$setDestroySpeed(float v);

	@Override
	@Accessor("requiresCorrectToolForDrops")
	@Mutable
	public abstract void kjs$setRequiresTool(boolean v);

	@Override
	@Accessor("lightEmission")
	@Mutable
	public abstract void kjs$setLightEmission(int v);
}
