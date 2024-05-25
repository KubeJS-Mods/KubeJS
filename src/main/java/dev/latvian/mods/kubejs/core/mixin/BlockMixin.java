package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
@RemapPrefixForJS("kjs$")
public abstract class BlockMixin extends BlockBehaviourMixin {
	@Override
	@Accessor("descriptionId")
	@Mutable
	public abstract void kjs$setNameKey(String key);
}
