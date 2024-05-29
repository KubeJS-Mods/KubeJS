package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ScriptFileInfo {
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w./]");
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\w+)\\s*[:=]?\\s*(-?\\w+)$");

	public final ScriptPackInfo pack;
	public final String file;
	public final ResourceLocation id;
	public final String location;
	private final Map<String, List<String>> properties;
	private int priority;
	private boolean ignored;
	private String packMode;
	private final Set<String> requiredMods;
	public String[] lines;

	public ScriptFileInfo(ScriptPackInfo p, String f) {
		pack = p;
		file = f;
		id = new ResourceLocation(pack.namespace, FILE_FIXER.matcher(pack.pathStart + file).replaceAll("_").toLowerCase());
		location = ID.string(pack.namespace + ":" + pack.pathStart + file);
		properties = new HashMap<>();
		priority = 0;
		ignored = false;
		packMode = "";
		requiredMods = new HashSet<>(0);
		lines = UtilsJS.EMPTY_STRING_ARRAY;
	}

	public void preload(ScriptSource source) throws Throwable {
		properties.clear();
		priority = 0;
		ignored = false;
		lines = source.readSource(this).toArray(UtilsJS.EMPTY_STRING_ARRAY);

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

		priority = Integer.parseInt(getProperty("priority", "0"));
		ignored = getProperty("ignored", "false").equals("true") || getProperty("ignore", "false").equals("true");
		packMode = getProperty("packmode", "");
		requiredMods.addAll(getProperties("requires"));
	}

	public List<String> getProperties(String s) {
		return properties.getOrDefault(s, List.of());
	}

	public String getProperty(String s, String def) {
		var l = getProperties(s);
		return l.isEmpty() ? def : l.get(l.size() - 1);
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
}