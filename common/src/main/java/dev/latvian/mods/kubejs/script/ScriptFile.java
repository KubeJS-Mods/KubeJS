package dev.latvian.mods.kubejs.script;

import java.nio.charset.StandardCharsets;

/**
 * @author LatvianModder
 */
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
		try (var stream = source.createStream(info)) {
			var script = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			pack.manager.context.evaluateString(pack.scope, script, info.location, 1, null);
		}
	}

	@Override
	public int compareTo(ScriptFile o) {
		return Integer.compare(o.info.getPriority(), info.getPriority());
	}
}