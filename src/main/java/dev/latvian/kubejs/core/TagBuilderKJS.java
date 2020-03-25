package dev.latvian.kubejs.core;

import net.minecraft.tags.Tag;

import java.util.Set;

/**
 * @author LatvianModder
 */
public interface TagBuilderKJS<T>
{
	Set<Tag.ITagEntry<T>> getKJSEntries();
}