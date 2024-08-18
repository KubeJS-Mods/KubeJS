package dev.latvian.mods.kubejs;

import net.minecraft.server.packs.PackType;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.io.IOException;
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

	Path GAMEDIR = FMLPaths.GAMEDIR.get().normalize().toAbsolutePath();
	Path DIRECTORY = dir(GAMEDIR.resolve("kubejs"), true);
	Path DATA = dir(DIRECTORY.resolve("data"));
	Path ASSETS = dir(DIRECTORY.resolve("assets"));
	Path STARTUP_SCRIPTS = DIRECTORY.resolve("startup_scripts");
	Path SERVER_SCRIPTS = DIRECTORY.resolve("server_scripts");
	Path CLIENT_SCRIPTS = DIRECTORY.resolve("client_scripts");
	Path CONFIG = dir(DIRECTORY.resolve("config"));
	Path COMMON_PROPERTIES = CONFIG.resolve("common.json");
	Path CLIENT_PROPERTIES = CONFIG.resolve("client.json");
	Path WEB_SERVER_PROPERTIES = CONFIG.resolve("web_server.json");
	Path CONFIG_DEV_PROPERTIES = CONFIG.resolve("dev.json");
	Path PACKICON = CONFIG.resolve("packicon.png");
	Path README = DIRECTORY.resolve("README.txt");
	Path LOCAL = dir(GAMEDIR.resolve("local").resolve("kubejs"));
	Path LOCAL_DEV_PROPERTIES = LOCAL.resolve("dev.json");
	Path EXPORT = dir(LOCAL.resolve("export"));
	Path EXPORTED_PACKS = dir(LOCAL.resolve("exported_packs"));
	Path LOCAL_STARTUP_SCRIPTS = dir(LOCAL.resolve("local_server_scripts"));
	Path LOCAL_SERVER_SCRIPTS = dir(LOCAL.resolve("local_server_scripts"));

	static Path get(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? ASSETS : DATA;
	}

	static Path getLocalDevProperties() {
		return CommonProperties.get().saveDevPropertiesInConfig ? CONFIG_DEV_PROPERTIES : LOCAL_DEV_PROPERTIES;
	}

	static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(GAMEDIR)) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}

		return path;
	}
}
