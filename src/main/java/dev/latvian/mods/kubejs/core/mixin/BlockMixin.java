package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockMixin extends BlockBehaviourMixin implements BlockKJS {
	@Shadow
	@Final
	private Holder.Reference<Block> builtInRegistryHolder;

	@Unique
	private ResourceKey<Block> kjs$registryKey;

	@Unique
	private String kjs$id;

	@Override
	public Holder.Reference<Block> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Block> kjs$getRegistryKey() {
		if (kjs$registryKey == null) {
			kjs$registryKey = super.kjs$getRegistryKey();
		}

		return kjs$registryKey;
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = super.kjs$getId();
		}

		return kjs$id;
	}

	@Override
	@Accessor("descriptionId")
	@Mutable
	public abstract void kjs$setNameKey(String key);
}
