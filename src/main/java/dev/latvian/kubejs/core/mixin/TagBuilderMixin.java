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
	@Final
	@Accessor(value = "proxyTags")
	public abstract List<ITag.Proxy> getProxyListKJS();
}