package dev.latvian.kubejs;

import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.server.packs.PackType;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author LatvianModder
 */
public class KubeJSPaths {
	public static final Path DIRECTORY = Platform.getGameFolder().resolve("kubejs").normalize();
	public static final Path DATA = DIRECTORY.resolve("data");
	public static final Path ASSETS = DIRECTORY.resolve("assets");
	public static final Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
	public static final Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
	public static final Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
	public static final Path CONFIG = DIRECTORY.resolve("config");
	public static final Path EXPORTED = DIRECTORY.resolve("exported");
	public static final Path README = DIRECTORY.resolve("README.txt");

	static {
		if (Files.notExists(DIRECTORY)) {
			UtilsJS.tryIO(() -> Files.createDirectories(DIRECTORY));
		}

		if (Files.notExists(CONFIG)) {
			UtilsJS.tryIO(() -> Files.createDirectories(CONFIG));
		}

		if (Files.notExists(EXPORTED)) {
			UtilsJS.tryIO(() -> Files.createDirectories(EXPORTED));
		}
	}

	public static Path get(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
	}
}
