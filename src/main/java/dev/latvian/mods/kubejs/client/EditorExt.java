package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.util.Util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class EditorExt {
	public static final String VSCODE = vsLikeScheme("vscode");
	public static final String VSCODIUM = vsLikeScheme("vscodium");
	public static final String VSCODE_OSS = vsLikeScheme("vscode-oss");

	private static String vsLikeScheme(String prefix) {
		return prefix + "://file{path}:{line}:{col}";
	}

	public static boolean isKnownVSCode() {
		var custom = DevProperties.get().openUriFormat;
		return !custom.isEmpty() && (custom.equals(VSCODE) || custom.equals(VSCODIUM) || custom.equals(VSCODE_OSS));
	}

	private static URI format(String scheme, Path path, int line, int column) throws URISyntaxException {
		// Normalize to use forward slash that works for Windows and Linux absolute paths
		String rawPath = path.toAbsolutePath().normalize().toUri().getRawPath();

		if (rawPath == null || rawPath.isEmpty()) {
			// Fallback: manually build something URI-legal if toUri() somehow gave nothing usable
			rawPath = path.toAbsolutePath().normalize().toString().replace('\\', '/');
			if (!rawPath.startsWith("/")) {
				rawPath = "/" + rawPath;
			}
		}

		String formatted = scheme
			.replace("{path}", rawPath)
			.replace("{line}", String.valueOf(line))
			.replace("{col}", String.valueOf(column));

		// URI#create would throw an IllegalArgumentException on any bad input
		// Use the checked constructor instead so callers can handle failure gracefully
		return new URI(formatted);
	}

	public static void openFile(Path path, int line, int column) {
		var custom = DevProperties.get().openUriFormat;
		if (!custom.isBlank()) {
			try {
				Util.getPlatform().openUri(format(custom, path, line, column));
				return;
			} catch (URISyntaxException | IllegalArgumentException e) {
				// Bad/unsupported openUriFormat so the game doesn't crash.
				// Just fall through to opening the file/folder directly instead.
				KubeJS.LOGGER.error("Failed to build editor URI for " + path + " with format '" + custom + "'", e);
			}
		}
		Util.getPlatform().openPath(path);
	}
}
