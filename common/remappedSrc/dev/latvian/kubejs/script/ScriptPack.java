package dev.latvian.kubejs.script;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPack
{
	public final ScriptManager manager;
	public final ScriptPackInfo info;
	public final List<ScriptFile> scripts;

	public Context context;
	public Scriptable scope;

	public ScriptPack(ScriptManager m, ScriptPackInfo i)
	{
		manager = m;
		info = i;
		scripts = new ArrayList<>();
	}
}