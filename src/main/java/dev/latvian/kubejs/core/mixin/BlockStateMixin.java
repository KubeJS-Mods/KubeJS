package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.BlockStateKJS;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(BlockState.class)
public abstract class BlockStateMixin implements BlockStateKJS
{
	@Override
	@Accessor("lightLevel")
	public abstract void setLightLevelKJS(int level);
}