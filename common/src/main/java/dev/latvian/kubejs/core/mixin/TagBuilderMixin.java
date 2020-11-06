package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagBuilderKJS;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.tags.Tag;

/**
 * @author LatvianModder
 */
@Mixin(Tag.Builder.class)
public abstract class TagBuilderMixin implements TagBuilderKJS
{
	@Override
	@Final
	@Accessor(value = "proxyTags")
	public abstract List<Tag.BuilderEntry> getProxyListKJS();
}