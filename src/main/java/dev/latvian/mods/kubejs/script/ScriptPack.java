package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Scriptable;

import java.util.ArrayList;
import java.util.List;

public class ScriptPack {
	public final ScriptManager manager;
	public final ScriptPackInfo info;
	public final List<ScriptFile> scripts;

	public Scriptable scope;

	public ScriptPack(ScriptManager m, ScriptPackInfo i) {
		manager = m;
		info = i;
		scripts = new ArrayList<>();
	}
}