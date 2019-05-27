package com.latmod.mods.kubejs;

import com.latmod.mods.kubejs.events.EventJS;

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