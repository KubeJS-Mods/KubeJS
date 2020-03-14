package dev.latvian.kubejs.mixin;

import dev.latvian.kubejs.server.TagBuilderAccess;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

/**
 * @author LatvianModder
 */
@Mixin(Tag.Builder.class)
public abstract class TagBuilderMixin<T> implements TagBuilderAccess<T>
{
	@Shadow
	private Set<Tag.ITagEntry<T>> entries;

	@Override
	public Set<Tag.ITagEntry<T>> getKJSEntries()
	{
		return entries;
	}
}