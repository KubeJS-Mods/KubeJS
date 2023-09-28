package dev.latvian.mods.kubejs;

import dev.architectury.platform.Platform;
import net.minecraft.server.packs.PackType;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.nio.file.Files;
import java.nio.file.Path;

public interface KubeJSPaths {
	MutableBoolean FIRST_RUN = new MutableBoolean(false);

	static Path dir(Path dir, boolean markFirstRun) {
		if (Files.notExists(dir)) {
			try {
				Files.createDirectories(dir);

				if (markFirstRun) {
					FIRST_RUN.setTrue();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return dir;
	}

	static Path dir(Path dir) {
		return dir(dir, false);
	}

	Path DIRECTORY = dir(Platform.getGameFolder().resolve("kubejs"), true);
	Path DATA = dir(DIRECTORY.resolve("data"));
	Path ASSETS = dir(DIRECTORY.resolve("assets"));
	Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
	Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
	Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
	Path CONFIG = dir(DIRECTORY.resolve("config"));
	Path COMMON_PROPERTIES = CONFIG.resolve("common.properties");
	Path CLIENT_PROPERTIES = CONFIG.resolve("client.properties");
	Path CONFIG_DEV_PROPERTIES = CONFIG.resolve("dev.properties");
	Path README = DIRECTORY.resolve("README.txt");
	Path LOCAL = dir(Platform.getGameFolder().resolve("local").resolve("kubejs"));
	Path LOCAL_CACHE = dir(LOCAL.resolve("cache"));
	Path LOCAL_DEV_PROPERTIES = LOCAL.resolve("dev.properties");
	Path EXPORT = dir(LOCAL.resolve("export"));
	Path EXPORTED_PACKS = dir(LOCAL.resolve("exported_packs"));

	static Path get(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
	}

	static Path getLocalDevProperties() {
		return CommonProperties.get().saveDevPropertiesInConfig ? CONFIG_DEV_PROPERTIES : LOCAL_DEV_PROPERTIES;
	}
}
