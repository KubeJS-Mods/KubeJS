package dev.latvian.kubejs.script;

import javax.script.ScriptEngine;
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

	public ScriptEngine engine;

	public ScriptPack(ScriptManager m, ScriptPackInfo i)
	{
		manager = m;
		info = i;
		scripts = new ArrayList<>();
	}
}