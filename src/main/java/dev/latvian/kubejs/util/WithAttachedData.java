package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.DocMethod;

/**
 * @author LatvianModder
 */
public interface WithAttachedData
{
	@DocMethod("Temporary data, mods can attach objects to this")
	AttachedData getData();
}