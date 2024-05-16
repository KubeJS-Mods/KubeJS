package dev.latvian.mods.kubejs.script;

import java.util.ArrayList;
import java.util.List;

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