package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType
{
	STARTUP("startup", "KubeJS Startup", () -> KubeJS.startupScriptManager),
	SERVER("server", "KubeJS Server", () -> ServerJS.instance.scriptManager),
	CLIENT("client", "KubeJS Client", () -> KubeJS.clientScriptManager);

	public static ScriptType of(IWorldReader world)
	{
		return world.isRemote() ? CLIENT : SERVER;
	}

	public final String name;
	public final ConsoleJS console;
	public final Supplier<ScriptManager> manager;

	ScriptType(String n, String cname, Supplier<ScriptManager> m)
	{
		name = n;
		console = new ConsoleJS(this, LogManager.getLogger(cname));
		manager = m;
	}
}