package dev.latvian.kubejs.server;

import net.minecraft.tags.Tag;

import java.util.Set;

/**
 * @author LatvianModder
 */
public interface TagBuilderAccess<T>
{
	Set<Tag.ITagEntry<T>> getKJSEntries();
}