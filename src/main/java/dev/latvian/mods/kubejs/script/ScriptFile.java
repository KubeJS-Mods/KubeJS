package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.UtilsJS;

public class ScriptFile implements Comparable<ScriptFile> {
	public final ScriptPack pack;
	public final ScriptFileInfo info;
	public final ScriptSource source;

	public ScriptFile(ScriptPack p, ScriptFileInfo i, ScriptSource s) {
		pack = p;
		info = i;
		source = s;
	}

	public void load() throws Throwable {
		pack.manager.context.evaluateString(pack.scope, String.join("\n", info.lines), info.location, 1, null);
		info.lines = UtilsJS.EMPTY_STRING_ARRAY; // free memory
	}

	@Override
	public int compareTo(ScriptFile o) {
		return Integer.compare(o.info.getPriority(), info.getPriority());
	}
}