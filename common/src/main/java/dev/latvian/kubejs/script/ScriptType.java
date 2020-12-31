package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.ConsoleJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.world.level.LevelReader;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType
{
	STARTUP("startup", "KubeJS Startup", () -> KubeJS.startupScriptManager),
	SERVER("server", "KubeJS Server", () -> ServerScriptManager.instance.scriptManager),
	CLIENT("client", "KubeJS Client", () -> KubeJS.clientScriptManager);

	public static ScriptType of(LevelReader world)
	{
		return world.isClientSide() ? CLIENT : SERVER;
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

	public Path getLogFile()
	{
		Path dir = Platform.getGameFolder().resolve("logs/kubejs");
		Path file = dir.resolve(name + ".txt");

		try
		{
			if (!Files.exists(dir))
			{
				Files.createDirectories(dir);
			}

			if (!Files.exists(file))
			{
				Files.createFile(file);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return file;
	}
}