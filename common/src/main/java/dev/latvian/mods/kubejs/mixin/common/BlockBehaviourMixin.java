package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
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
@RemapPrefixForJS("kjs$")
public abstract class BlockBehaviourMixin implements BlockKJS {
	@Unique
	private BlockBuilder blockBuilderKJS;

	@Unique
	private final CompoundTag typeDataKJS = new CompoundTag();

	@Override
	@Nullable
	public BlockBuilder kjs$getBlockBuilder() {
		return blockBuilderKJS;
	}

	@Override
	public void kjs$setBlockBuilder(BlockBuilder b) {
		blockBuilderKJS = b;
	}

	@Override
	public CompoundTag kjs$getTypeData() {
		return typeDataKJS;
	}

	@Override
	@Accessor("material")
	@Mutable
	public abstract void kjs$setMaterialRaw(Material v);

	@Override
	@Accessor("hasCollision")
	@Mutable
	public abstract void kjs$setHasCollision(boolean v);

	@Override
	@Accessor("explosionResistance")
	@Mutable
	public abstract void kjs$setExplosionResistance(float v);

	@Override
	@Accessor("isRandomlyTicking")
	@Mutable
	public abstract void kjs$setIsRandomlyTicking(boolean v);

	@Override
	@Accessor("soundType")
	@Mutable
	public abstract void kjs$setSoundType(SoundType v);

	@Override
	@Accessor("friction")
	@Mutable
	public abstract void kjs$setFriction(float v);

	@Override
	@Accessor("speedFactor")
	@Mutable
	public abstract void kjs$setSpeedFactor(float v);

	@Override
	@Accessor("jumpFactor")
	@Mutable
	public abstract void kjs$setJumpFactor(float v);
}
