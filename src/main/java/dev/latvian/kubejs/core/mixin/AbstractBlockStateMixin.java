package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.AbstractBlockStateKJS;
import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin implements AbstractBlockStateKJS
{
	@Override
	@Accessor("lightLevel")
	public abstract void setLightLevelKJS(int level);
}