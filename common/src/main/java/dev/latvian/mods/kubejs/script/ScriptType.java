package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.level.LevelReader;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType {
	STARTUP("startup", "KubeJS Startup", KubeJS::getStartupScriptManager),
	SERVER("server", "KubeJS Server", ServerScriptManager::getScriptManager),
	CLIENT("client", "KubeJS Client", KubeJS::getClientScriptManager);

	static {
		ConsoleJS.STARTUP = STARTUP.console;
		ConsoleJS.SERVER = SERVER.console;
		ConsoleJS.CLIENT = CLIENT.console;
	}

	public static final ScriptType[] VALUES = values();

	public static ScriptType of(LevelReader level) {
		return level.isClientSide() ? CLIENT : SERVER;
	}

	public static ScriptType getCurrent(ScriptType def) {
		Context cx = Context.getCurrentContext();

		if (cx != null && cx.sharedContextData.getExtraProperty("Type") instanceof ScriptType t) {
			return t;
		}

		return def;
	}

	public final String name;
	public final transient List<String> errors;
	public final transient List<String> warnings;
	public final ConsoleJS console;
	public final transient Supplier<ScriptManager> manager;
	public final transient ExecutorService executor;

	ScriptType(String n, String cname, Supplier<ScriptManager> m) {
		name = n;
		errors = new ArrayList<>();
		warnings = new ArrayList<>();
		console = new ConsoleJS(this, LoggerFactory.getLogger(cname));
		manager = m;
		executor = Executors.newSingleThreadExecutor();
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

		for (var group : EventGroup.getGroups().values()) {
			for (var handler : group.getHandlers().values()) {
				handler.clear(this);
			}
		}
	}

	public Component errorsComponent(String command) {
		return Component.literal("KubeJS errors found [" + errors.size() + "]! Run '" + command + "' for more info")
				.kjs$click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
				.kjs$hover(Component.literal("Click to show"))
				.withStyle(ChatFormatting.DARK_RED);
	}

	public Component warningsComponent(String command) {
		return Component.literal("KubeJS warnings found [" + warnings.size() + "]! Run '" + command + "' for more info")
				.kjs$click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
				.kjs$hover(Component.literal("Click to show"))
				.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500)));
	}
}