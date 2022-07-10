package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.level.LevelReader;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	public final transient List<String> errors;
	public final transient List<String> warnings;
	public final ConsoleJS console;
	public final transient Supplier<ScriptManager> manager;
	public final transient ExecutorService executor;
	public final transient Map<String, EventHandler> eventHandlers;

	ScriptType(String n, String cname, Supplier<ScriptManager> m) {
		name = n;
		errors = new ArrayList<>();
		warnings = new ArrayList<>();
		console = new ConsoleJS(this, LoggerFactory.getLogger(cname));
		manager = m;
		executor = Executors.newSingleThreadExecutor();
		eventHandlers = new HashMap<>();
	}

	public Path getLogFile() {
		var dir = Platform.getGameFolder().resolve("logs/kubejs");
		var file = dir.resolve(name + ".txt");

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

	@HideFromJS
	public void unload() {
		errors.clear();
		warnings.clear();
		console.resetFile();
		eventHandlers.values().forEach(EventHandler::clear);
	}
}