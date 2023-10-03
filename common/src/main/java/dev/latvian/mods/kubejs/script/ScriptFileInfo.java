package dev.latvian.mods.kubejs.script;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ScriptFileInfo {
	private static final Pattern FILE_FIXER = Pattern.compile("[^\\w.\\/]");
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^//\\s*(\\w+)\\s*:?\\s*(\\w+)$");

	public final ScriptPackInfo pack;
	public final String file;
	public final ResourceLocation id;
	public final String location;
	private final Map<String, List<String>> properties;
	private int priority;
	private boolean ignored;
	private String packMode;
	private final Set<String> requiredMods;

	public ScriptFileInfo(ScriptPackInfo p, String f) {
		pack = p;
		file = f;
		id = new ResourceLocation(pack.namespace, FILE_FIXER.matcher(pack.pathStart + file).replaceAll("_").toLowerCase());
		location = UtilsJS.getID(pack.namespace + ":" + pack.pathStart + file);
		properties = new HashMap<>();
		priority = 0;
		ignored = false;
		packMode = "";
		requiredMods = new HashSet<>(0);
	}

	@Nullable
	public Throwable preload(ScriptSource source) {
		properties.clear();
		priority = 0;
		ignored = false;

		try (var reader = new BufferedReader(new InputStreamReader(source.createStream(this), StandardCharsets.UTF_8))) {
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) {
					continue;
				}

				var matcher = PROPERTY_PATTERN.matcher(line);

				if (matcher.find()) {
					properties.computeIfAbsent(matcher.group(1).trim(), k -> new ArrayList<>()).add(matcher.group(2).trim());
				} else {
					break;
				}
			}

			priority = Integer.parseInt(getProperty("priority", "0"));
			ignored = getProperty("ignored", "false").equals("true") || getProperty("ignore", "false").equals("true");
			packMode = getProperty("packmode", "");
			requiredMods.addAll(getProperties("requires"));
			return null;
		} catch (Throwable ex) {
			return ex;
		}
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

	public boolean skipLoading() {
		if (ignored) {
			return true;
		}

		if (!packMode.isEmpty() && !packMode.equals(CommonProperties.get().packMode)) {
			return true;
		}

		if (!requiredMods.isEmpty()) {
			for (String mod : requiredMods) {
				if (!Platform.isModLoaded(mod)) {
					return true;
				}
			}
		}

		return false;
	}
}
