package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagBuilderKJS;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

/**
 * @author LatvianModder
 */
@Mixin(Tag.Builder.class)
public abstract class TagBuilderMixin<T> implements TagBuilderKJS<T>
{
	@Shadow
	@Final
	private Set<Tag.ITagEntry<T>> entries;

	@Override
	public Set<Tag.ITagEntry<T>> getKJSEntries()
	{
		return entries;
	}
}