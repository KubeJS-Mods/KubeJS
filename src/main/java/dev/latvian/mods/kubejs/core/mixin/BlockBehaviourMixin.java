package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BlockBehaviour.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockBehaviourMixin implements BlockKJS {
	@Unique
	private BlockBuilder kjs$blockBuilder;

	@Unique
	private CompoundTag kjs$typeData;

	@Unique
	private Consumer<RandomTickCallbackJS> kjs$randomTickCallback;

	@Override
	@Nullable
	public BlockBuilder kjs$getBlockBuilder() {
		return kjs$blockBuilder;
	}

	@Override
	public void kjs$setBlockBuilder(BlockBuilder b) {
		kjs$blockBuilder = b;
	}

	@Override
	public CompoundTag kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new CompoundTag();
		}

		return kjs$typeData;
	}

	@Override
	public void kjs$setRandomTickCallback(Consumer<RandomTickCallbackJS> callback) {
		kjs$setIsRandomlyTicking(true);
		this.kjs$randomTickCallback = callback;
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	private void onRandomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
		if (kjs$randomTickCallback != null) {
			kjs$randomTickCallback.accept(new RandomTickCallbackJS(new BlockContainerJS(serverLevel, blockPos), randomSource));
			ci.cancel();
		}
	}

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
