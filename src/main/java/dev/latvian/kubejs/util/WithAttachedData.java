package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.Info;

/**
 * @author LatvianModder
 */
public interface WithAttachedData
{
	@Info("Temporary data, mods can attach objects to this")
	AttachedData getData();
}