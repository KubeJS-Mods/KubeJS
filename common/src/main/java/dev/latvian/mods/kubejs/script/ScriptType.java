package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.world.level.LevelReader;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType {
	STARTUP("startup", "KubeJS Startup", () -> KubeJS.startupScriptManager),
	SERVER("server", "KubeJS Server", () -> ServerScriptManager.instance.scriptManager),
	CLIENT("client", "KubeJS Client", () -> KubeJS.clientScriptManager);

	static {
		ConsoleJS.STARTUP = STARTUP.console;
		ConsoleJS.SERVER = SERVER.console;
		ConsoleJS.CLIENT = CLIENT.console;
	}

	public static ScriptType of(LevelReader level) {
		return level.isClientSide() ? CLIENT : SERVER;
	}

	public final String name;
	public final List<String> errors;
	public final List<String> warnings;
	public final ConsoleJS console;
	public final Supplier<ScriptManager> manager;

	ScriptType(String n, String cname, Supplier<ScriptManager> m) {
		name = n;
		errors = new ArrayList<>();
		warnings = new ArrayList<>();
		console = new ConsoleJS(this, LogManager.getLogger(cname));
		manager = m;
	}

	public Path getLogFile() {
		Path dir = Platform.getGameFolder().resolve("logs/kubejs");
		Path file = dir.resolve(name + ".txt");

		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}

			if (!Files.exists(file)) {
				Files.createFile(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return file;
	}

	public boolean isClient() {
		return this == CLIENT;
	}

	public boolean isServer() {
		return this == SERVER;
	}
}