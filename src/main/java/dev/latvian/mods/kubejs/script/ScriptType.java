package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public enum ScriptType implements ScriptTypePredicate, ScriptTypeHolder {
	STARTUP("startup", "KubeJS Startup", KubeJSPaths.STARTUP_SCRIPTS, KubeJS::getStartupScriptManager),
	SERVER("server", "KubeJS Server", KubeJSPaths.SERVER_SCRIPTS, ServerScriptManager::getScriptManager),
	CLIENT("client", "KubeJS Client", KubeJSPaths.CLIENT_SCRIPTS, KubeJS::getClientScriptManager);


	static {
		ConsoleJS.STARTUP = STARTUP.console;
		ConsoleJS.SERVER = SERVER.console;
		ConsoleJS.CLIENT = CLIENT.console;
	}

	public static final ScriptType[] VALUES = values();

	public final String name;
	public final ConsoleJS console;
	public final Path path;
	public final String nameStrip;
	public final transient Supplier<ScriptManager> manager;
	public transient Executor executor;

	ScriptType(String n, String cname, Path path, Supplier<ScriptManager> m) {
		this.name = n;
		this.console = new ConsoleJS(this, LoggerFactory.getLogger(cname));
		this.path = path;
		this.nameStrip = name + "_scripts:";
		this.manager = m;
		this.executor = Runnable::run;
	}

	public Path getLogFile() {
		var dir = FMLPaths.GAMEDIR.get().resolve("logs/kubejs");
		var file = dir.resolve(name + ".log");

		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}

			if (!Files.exists(file)) {
				var oldFile = dir.resolve(name + ".txt");

				if (Files.exists(oldFile)) {
					Files.move(oldFile, file);
				} else {
					Files.createFile(file);
				}
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

	public boolean isStartup() {
		return this == STARTUP;
	}

	@HideFromJS
	public void unload() {
		console.resetFile();

		for (var group : EventGroups.ALL.get().map().values()) {
			for (var handler : group.getHandlers().values()) {
				handler.clear(this);
			}
		}
	}

	@Override
	public boolean test(ScriptType type) {
		return type == this;
	}

	@Override
	public List<ScriptType> getValidTypes() {
		return List.of(this);
	}

	@NotNull
	@Override
	public ScriptTypePredicate negate() {
		return switch (this) {
			case STARTUP -> ScriptTypePredicate.COMMON;
			case SERVER -> ScriptTypePredicate.STARTUP_OR_CLIENT;
			case CLIENT -> ScriptTypePredicate.STARTUP_OR_SERVER;
		};
	}

	@Override
	public ScriptType kjs$getScriptType() {
		return this;
	}
}