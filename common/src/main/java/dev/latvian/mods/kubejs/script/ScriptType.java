package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public enum ScriptType implements ScriptTypePredicate {
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
		return level == null || level.isClientSide() ? CLIENT : SERVER;
	}

	public static ScriptType of(Entity entity) {
		return entity instanceof ServerPlayer ? SERVER : entity == null || entity.level.isClientSide() ? CLIENT : SERVER;
	}

	public static ScriptType getCurrent(Context cx) {
		return (ScriptType) cx.getProperty("Type");
	}

	public final String name;
	public final transient ConcurrentLinkedDeque<String> errors;
	public final transient ConcurrentLinkedDeque<String> warnings;
	public final ConsoleJS console;
	public final transient Supplier<ScriptManager> manager;
	public transient Executor executor;

	ScriptType(String n, String cname, Supplier<ScriptManager> m) {
		name = n;
		errors = new ConcurrentLinkedDeque<>();
		warnings = new ConcurrentLinkedDeque<>();
		console = new ConsoleJS(this, LoggerFactory.getLogger(cname));
		manager = m;
		executor = Runnable::run;
	}

	public Path getLogFile() {
		var dir = Platform.getGameFolder().resolve("logs/kubejs");
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
				.kjs$clickRunCommand(command)
				.kjs$hover(Component.literal("Click to show"))
				.withStyle(ChatFormatting.DARK_RED);
	}

	public Component warningsComponent(String command) {
		return Component.literal("KubeJS warnings found [" + warnings.size() + "]! Run '" + command + "' for more info")
				.kjs$clickRunCommand(command)
				.kjs$hover(Component.literal("Click to show"))
				.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFA500)));
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
}