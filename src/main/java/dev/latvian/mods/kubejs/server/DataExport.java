package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.TimeJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DataExport {
	@HideFromJS
	public static DataExport export = null;

	public CommandSourceStack source;

	private final Map<String, Callable<byte[]>> exportedFiles = new ConcurrentHashMap<>();

	public static void exportData() {
		if (export != null) {
			try {
				export.exportData0();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			export = null;
		}
	}

	public void add(String path, Callable<byte[]> data) {
		try {
			exportedFiles.put(path, data);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addString(String path, String data) {
		add(path, () -> data.getBytes(StandardCharsets.UTF_8));
	}

	public void addJson(String path, JsonElement json) {
		add(path, () -> JsonUtils.toPrettyString(json).getBytes(StandardCharsets.UTF_8));
	}

	private void appendLine(StringBuilder sb, Calendar calendar, ConsoleLine line) {
		calendar.setTimeInMillis(line.timestamp);
		sb.append('[');
		TimeJS.appendTimestamp(sb, calendar);
		sb.append(']');
		sb.append(' ');
		sb.append('[');
		sb.append(line.type);
		sb.append(']');
		sb.append(' ');

		if (line.type == LogType.ERROR) {
			sb.append('!');
			sb.append(' ');
		}

		sb.append(line.getText());
		sb.append('\n');
	}

	@SuppressWarnings({"resource", "ResultOfMethodCallIgnored"})
	private void exportData0() throws Exception {
		source.registryAccess().registries().forEach(reg -> {
			var key = reg.key();
			var registry = reg.value();

			var j = new JsonObject();

			for (var entry : registry.entrySet()) {
				j.addProperty(entry.getKey().location().toString(), (entry.getValue() == null ? "null" : entry.getValue().getClass().getName()));
			}

			addJson("registries/" + key.location().getPath() + ".json", j);
		});

		var logStringBuilder = new StringBuilder();
		var calendar = Calendar.getInstance();

		for (var line : ConsoleJS.SERVER.errors) {
			appendLine(logStringBuilder, calendar, line);
		}

		if (logStringBuilder.length() > 0) {
			logStringBuilder.setLength(logStringBuilder.length() - 1);
			addString("errors.log", logStringBuilder.toString());
		}

		logStringBuilder.setLength(0);

		for (var line : ConsoleJS.SERVER.warnings) {
			appendLine(logStringBuilder, calendar, line);
		}

		if (logStringBuilder.length() > 0) {
			logStringBuilder.setLength(logStringBuilder.length() - 1);
			addString("warnings.log", logStringBuilder.toString());
		}

		var modArr = new JsonArray();

		for (var mod : ModList.get().getMods()) {
			var o = new JsonObject();
			o.addProperty("id", mod.getModId().trim());
			o.addProperty("name", mod.getDisplayName().trim());
			o.addProperty("version", mod.getVersion().toString().trim());
			o.addProperty("description", mod.getDescription().trim());
			// FIXME
			// o.addProperty("authors", String.join(", ", mod.getAuthors()).trim());
			// o.addProperty("homepage", mod.getHomepage().orElse("").trim());
			// o.addProperty("sources", mod.getSources().orElse("").trim());
			// o.addProperty("issue_tracker", mod.getIssueTracker().orElse("").trim());
			// o.addProperty("license", mod.getLicense() == null ? "" : String.join(", ", mod.getLicense()).trim());
			o.entrySet().removeIf(e -> e.getValue() instanceof JsonPrimitive p && p.isString() && p.getAsString().isEmpty());
			modArr.add(o);
		}

		addJson("mods.json", modArr);

		KubeJSPlugins.forEachPlugin(this, KubeJSPlugin::exportServerData);

		var index = new JsonArray();

		exportedFiles.keySet()
			.stream()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.forEach(index::add);

		addJson("index.json", index);

		var exportedFilePaths = new HashSet<String>();

		for (var file : exportedFiles.keySet()) {
			exportedFilePaths.add(file.replace(':', '/'));
		}

		Files.walk(KubeJSPaths.EXPORT)
			.sorted(Comparator.reverseOrder())
			.filter(path -> {
				if (Files.isDirectory(path)) {
					return true;
				}

				return !exportedFilePaths.contains(KubeJSPaths.EXPORT.relativize(path).toString().replace('\\', '/'));
			})
			.map(Path::toFile)
			.forEach(file -> {
				if (file.isFile()) {
					file.delete();
					KubeJS.LOGGER.info("Deleted old file " + file.getPath());
				} else if (file.isDirectory() && file.list().length == 0) {
					file.delete();
					KubeJS.LOGGER.info("Deleted empty directory " + file.getPath());
				}
			});

		if (Files.notExists(KubeJSPaths.EXPORT)) {
			Files.createDirectory(KubeJSPaths.EXPORT);
		}

		var arr = new CompletableFuture[exportedFiles.size()];
		int i = 0;

		for (var entry : exportedFiles.entrySet()) {
			arr[i++] = CompletableFuture.runAsync(() -> {
				try {
					var path = KubeJSPaths.EXPORT.resolve(entry.getKey().replace(':', '/'));
					var parent = path.getParent();

					if (Files.notExists(parent)) {
						Files.createDirectories(parent);
					}

					if (Files.notExists(path)) {
						Files.createFile(path);
					}

					Files.write(path, entry.getValue().call());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}, Util.ioPool());
		}

		CompletableFuture.allOf(arr).join();

		if (source.getServer().isSingleplayer()) {
			source.sendSuccess(() -> Component.literal("Done! Export in local/kubejs/export").kjs$clickOpenFile(KubeJSPaths.EXPORT.toAbsolutePath().toString()), false);
		} else {
			source.sendSuccess(() -> Component.literal("Done! Export in local/kubejs/export"), false);
		}
	}
}