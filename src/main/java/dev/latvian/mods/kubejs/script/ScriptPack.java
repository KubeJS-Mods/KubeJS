package dev.latvian.mods.kubejs.script;

import java.util.ArrayList;
import java.util.List;

/// A named collection of [ScriptFile]s loaded from a single directory.
///
/// Each pack corresponds to one source directory (e.g. `kubejs/server_scripts/`) and is
/// identified by its namespace (the directory name). Multiple packs can coexist in one
/// [ScriptManager], which allows mods and datapacks to ship their own script packs alongside the user's.
public class ScriptPack {
	public final ScriptManager manager;
	public final ScriptPackInfo info;
	public final List<ScriptFile> scripts;

	public ScriptPack(ScriptManager m, ScriptPackInfo i) {
		manager = m;
		info = i;
		scripts = new ArrayList<>();
	}
}