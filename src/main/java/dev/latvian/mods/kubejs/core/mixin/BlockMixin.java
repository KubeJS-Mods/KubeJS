package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockMixin implements BlockKJS {
	@Shadow
	@Final
	private Holder.Reference<Block> builtInRegistryHolder;

	@Unique
	private String kjs$id;

	@Unique
	private BlockBuilder kjs$blockBuilder;

	@Override
	public Holder.Reference<Block> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Block> kjs$getKey() {
		return builtInRegistryHolder.key();
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = builtInRegistryHolder.key().location().toString();
		}

		return kjs$id;
	}

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
	@Accessor("descriptionId")
	@Mutable
	public abstract void kjs$setNameKey(String key);

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void kjs$getName(CallbackInfoReturnable<MutableComponent> cir) {
		if (kjs$blockBuilder != null && kjs$blockBuilder.displayName != null && kjs$blockBuilder.formattedDisplayName) {
			cir.setReturnValue(Component.literal("").append(kjs$blockBuilder.displayName));
		}
	}
}
