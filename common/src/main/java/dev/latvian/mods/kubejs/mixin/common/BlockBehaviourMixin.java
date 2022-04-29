package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements BlockKJS {
	@Unique
	private BlockBuilder blockBuilderKJS;

	@Unique
	private final CompoundTag typeDataKJS = new CompoundTag();

	@Override
	@Nullable
	public BlockBuilder getBlockBuilderKJS() {
		return blockBuilderKJS;
	}

	@Override
	public void setBlockBuilderKJS(BlockBuilder b) {
		blockBuilderKJS = b;
	}

	@Override
	@RemapForJS("getTypeData")
	public CompoundTag getTypeDataKJS() {
		return typeDataKJS;
	}

	@Override
	@Accessor("material")
	@Mutable
	public abstract void setMaterialKJS(Material v);

	@Override
	@Accessor("hasCollision")
	@Mutable
	public abstract void setHasCollisionKJS(boolean v);

	@Override
	@Accessor("explosionResistance")
	@Mutable
	public abstract void setExplosionResistanceKJS(float v);

	@Override
	@Accessor("isRandomlyTicking")
	@Mutable
	public abstract void setIsRandomlyTickingKJS(boolean v);

	@Override
	@Accessor("soundType")
	@Mutable
	public abstract void setSoundTypeKJS(SoundType v);

	@Override
	@Accessor("friction")
	@Mutable
	public abstract void setFrictionKJS(float v);

	@Override
	@Accessor("speedFactor")
	@Mutable
	public abstract void setSpeedFactorKJS(float v);

	@Override
	@Accessor("jumpFactor")
	@Mutable
	public abstract void setJumpFactorKJS(float v);
}
