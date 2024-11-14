package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Block.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockMixin implements BlockKJS {
	@Unique
	private String kjs$id;

	@Unique
	private BlockBuilder kjs$blockBuilder;

	@Unique
	private Map<String, Object> kjs$typeData;

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = kjs$getBlock().builtInRegistryHolder().key().location().toString();
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
	public Map<String, Object> kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new HashMap<>();
		}

		return kjs$typeData;
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
