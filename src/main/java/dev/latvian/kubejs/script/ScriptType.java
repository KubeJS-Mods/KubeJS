package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.world.IWorldReader;
import org.apache.logging.log4j.LogManager;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType
{
	STARTUP("startup", "KubeJS Startup", () -> KubeJS.startupScriptManager, () -> true),
	SERVER("server", "KubeJS Server", () -> ServerJS.instance.scriptManager, () -> ServerJS.instance.debugLog),
	CLIENT("client", "KubeJS Client", () -> KubeJS.clientScriptManager, () -> true);

	public static ScriptType of(IWorldReader world)
	{
		return world.isRemote() ? CLIENT : SERVER;
	}

	public final String name;
	public final ConsoleJS console;
	public final BooleanSupplier isDebugConsole;
	public final ConsoleJS debugConsole;
	public final Supplier<ScriptManager> manager;

	ScriptType(String n, String cname, Supplier<ScriptManager> m, BooleanSupplier debug)
	{
		name = n;
		console = new ConsoleJS(this, LogManager.getLogger(cname));
		isDebugConsole = debug;

		debugConsole = new ConsoleJS(this, console.logger)
		{
			@Override
			protected boolean shouldPrint()
			{
				return isDebugConsole.getAsBoolean();
			}

			@Override
			public boolean isDebug()
			{
				return true;
			}
		};

		manager = m;
	}
}