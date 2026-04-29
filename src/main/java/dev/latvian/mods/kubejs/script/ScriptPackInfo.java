package dev.latvian.mods.kubejs.script;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/// Metadata for a [ScriptPack]: stores its namespace, display name, and a list of discovered [file][ScriptFileInfo]s.
public class ScriptPackInfo {
	public final String namespace;
	public final Component displayName;
	public final List<ScriptFileInfo> scripts;
	public final String pathStart;

	public ScriptPackInfo(String n, String p) {
		namespace = n;
		scripts = new ArrayList<>();
		pathStart = p;
		displayName = Component.literal(namespace); // Load custom properties
	}
}