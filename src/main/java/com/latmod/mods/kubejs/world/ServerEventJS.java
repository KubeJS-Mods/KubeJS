package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.events.EventJS;
import com.latmod.mods.kubejs.util.ServerJS;

/**
 * @author LatvianModder
 */
public class ServerEventJS extends EventJS
{
	public final ServerJS server;

	public ServerEventJS(ServerJS s)
	{
		server = s;
	}
}