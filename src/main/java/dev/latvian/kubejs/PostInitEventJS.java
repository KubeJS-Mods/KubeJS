package dev.latvian.kubejs;

import dev.latvian.kubejs.event.EventJS;

import java.util.Set;

/**
 * @author LatvianModder
 */
public class PostInitEventJS extends EventJS
{
	public final Set<String> loadedMods;

	public PostInitEventJS(Set<String> m)
	{
		loadedMods = m;
	}
}