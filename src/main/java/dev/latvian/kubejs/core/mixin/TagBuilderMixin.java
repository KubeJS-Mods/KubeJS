package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagBuilderKJS;
import net.minecraft.tags.ITag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(ITag.Builder.class)
public abstract class TagBuilderMixin implements TagBuilderKJS
{
	@Override
	@Accessor(value = "field_232953_a_", remap = false)
	@Final
	public abstract List<ITag.Proxy> getProxyListKJS();
}