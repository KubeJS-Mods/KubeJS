package dev.latvian.mods.kubejs.client;

import net.minecraft.Util;

import java.net.URI;
import java.nio.file.Path;

public class VSCodeExt {
	private static String path = null;
	private static String version = null;

	public static String getVSCodePath() {
		if (path == null) {
			path = System.getProperty("kubejs.vscode.path");

			if (path == null || path.isEmpty()) {
				path = switch (Util.getPlatform()) {
					case WINDOWS -> System.getenv("LOCALAPPDATA") + "\\Programs\\Microsoft VS Code\\bin\\code.cmd";
					case OSX -> "/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code";
					default -> "/usr/bin/code";
				};
			}
		}

		return path;
	}

	public static String getVersion() {
		if (version == null) {
			version = "";

			try {
				var process = new ProcessBuilder(getVSCodePath(), "--version").redirectErrorStream(true).start();
				int result = process.waitFor();

				if (result == 0) {
					try (var in = process.inputReader()) {
						version = in.readLine().trim();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		return version;
	}

	public static boolean isInstalled() {
		return !getVersion().isEmpty();
	}

	public static void openFile(Path path, int line, int column) {
		Util.getPlatform().openUri(URI.create("vscode://file/" + path.toAbsolutePath().toString().replace('\\', '/') + ":" + line + ":" + column));
	}
}
