package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.neoforged.fml.ModList;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ScriptFile implements Comparable<ScriptFile> {
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\w+)\\s*[:=]?\\s*(-?\\w+)$");

	public final ScriptPack pack;
	public final ScriptFileInfo info;

	private final Map<String, List<String>> properties;
	private int priority;
	private boolean ignored;
	private String packMode;
	private final Set<String> requiredMods;
	public String[] lines;
	public long lastModified;

	public ScriptFile(ScriptPack pack, ScriptFileInfo info) throws Exception {
		this.pack = pack;
		this.info = info;

		this.properties = new HashMap<>();
		this.priority = 0;
		this.ignored = false;
		this.packMode = "";
		this.requiredMods = new HashSet<>(0);

		this.lines = Files.readAllLines(info.path).toArray(UtilsJS.EMPTY_STRING_ARRAY);

		try {
			this.lastModified = Files.getLastModifiedTime(this.info.path).toMillis();
		} catch (Exception ex) {
			this.lastModified = 0L;
		}

		for (int i = 0; i < lines.length; i++) {
			var tline = lines[i].trim();

			if (tline.isEmpty() || tline.startsWith("import ")) {
				lines[i] = "";
			} else if (tline.startsWith("//")) {
				var matcher = PROPERTY_PATTERN.matcher(tline.substring(2).trim());

				if (matcher.find()) {
					properties.computeIfAbsent(matcher.group(1).trim(), k -> new ArrayList<>()).add(matcher.group(2).trim());
				}

				lines[i] = "";
			}
		}

		this.priority = Integer.parseInt(getProperty("priority", "0"));
		this.ignored = getProperty("ignored", "false").equals("true") || getProperty("ignore", "false").equals("true");
		this.packMode = getProperty("packmode", "");
		this.requiredMods.addAll(getProperties("requires"));
	}

	public void load(KubeJSContext cx) throws Throwable {
		cx.evaluateString(cx.topLevelScope, String.join("\n", lines), info.location, 1, null);
		lines = UtilsJS.EMPTY_STRING_ARRAY; // free memory
	}

	public List<String> getProperties(String s) {
		return properties.getOrDefault(s, List.of());
	}

	public String getProperty(String s, String def) {
		var l = getProperties(s);
		return l.isEmpty() ? def : l.getLast();
	}

	public int getPriority() {
		return priority;
	}

	public String skipLoading() {
		if (ignored) {
			return "Ignored";
		}

		if (!packMode.isEmpty() && !packMode.equals(CommonProperties.get().packMode)) {
			return "Pack mode mismatch";
		}

		if (!requiredMods.isEmpty()) {
			for (String mod : requiredMods) {
				if (!ModList.get().isLoaded(mod)) {
					return "Mod " + mod + " is not loaded";
				}
			}
		}

		return "";
	}

	@Override
	public int compareTo(ScriptFile o) {
		return Integer.compare(o.priority, priority);
	}
}