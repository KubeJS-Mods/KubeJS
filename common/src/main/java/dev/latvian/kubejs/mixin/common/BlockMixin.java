package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(Block.class)
public class BlockMixin {
	@RemapForJS("getId")
	public String getIdKJS() {
		return KubeJSRegistries.blocks().getId((Block) (Object) this).toString();
	}
}
