package dev.latvian.mods.kubejs.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
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

	private static <T> void addRegistry(JsonObject o, String name, Registrar<T> r) {
		var a = new JsonArray();

		for (var id : r.getIds()) {
			a.add(id.toString());
		}

		o.add(name, a);
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

	@SuppressWarnings({"rawtypes", "unchecked", "resource", "ResultOfMethodCallIgnored"})
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

		addString("errors.log", String.join("\n", ScriptType.SERVER.errors));
		addString("warnings.log", String.join("\n", ScriptType.SERVER.warnings));

		var modArr = new JsonArray();

		for (var mod : Platform.getMods()) {
			var o = new JsonObject();
			o.addProperty("id", mod.getModId().trim());
			o.addProperty("name", mod.getName().trim());
			o.addProperty("version", mod.getVersion().trim());
			o.addProperty("description", mod.getDescription().trim());
			o.addProperty("authors", String.join(", ", mod.getAuthors()).trim());
			o.addProperty("homepage", mod.getHomepage().orElse("").trim());
			o.addProperty("sources", mod.getSources().orElse("").trim());
			o.addProperty("issue_tracker", mod.getIssueTracker().orElse("").trim());
			o.addProperty("license", mod.getLicense() == null ? "" : String.join(", ", mod.getLicense()).trim());
			o.entrySet().removeIf(e -> e.getValue() instanceof JsonPrimitive p && p.isString() && p.getAsString().isEmpty());
			modArr.add(o);
		}

		addJson("mods.json", modArr);

		KubeJSPlugins.forEachPlugin(p -> p.exportServerData(this));

		var index = new JsonArray();

		exportedFiles.keySet()
			.stream()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.forEach(index::add);

		addJson("index.json", index);

		Files.walk(KubeJSPaths.EXPORT)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);

		Files.createDirectory(KubeJSPaths.EXPORT);

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