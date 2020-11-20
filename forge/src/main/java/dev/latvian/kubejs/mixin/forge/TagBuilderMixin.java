package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.core.TagBuilderKJS;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(Tag.Builder.class)
public abstract class TagBuilderMixin implements TagBuilderKJS
{
	@Override
	@Final
	@Accessor(value = "entries")
	public abstract List<ITag.Proxy> getProxyListKJS();
}