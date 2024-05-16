package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.util.RemapForJS;

/**
 * @author LatvianModder
 */
public interface Copyable {
	@RemapForJS("copy")
	Copyable copyJS();
}