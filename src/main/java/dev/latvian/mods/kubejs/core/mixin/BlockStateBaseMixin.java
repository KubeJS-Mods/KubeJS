package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.BlockStateKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockStateBaseMixin implements BlockStateKJS {
	@Shadow
	protected abstract BlockState asState();

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

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	private void kjs$onRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (kjs$randomTickOverride(asState(), level, pos, random)) {
			ci.cancel();
		}
	}
}
