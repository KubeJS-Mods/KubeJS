package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagBuilderKJS;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * @author LatvianModder
 */
@Mixin(Tag.Builder.class)
public abstract class TagBuilderMixin<T> implements TagBuilderKJS<T>
{
	@Override
	@Accessor("entries")
	public abstract Set<Tag.ITagEntry<T>> getEntriesKJS();
}