package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(value = Block.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public class BlockMixin {
	public String kjs$getId() {
		return String.valueOf(KubeJSRegistries.blocks().getId((Block) (Object) this));
	}
}
