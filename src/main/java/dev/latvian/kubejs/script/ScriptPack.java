package dev.latvian.kubejs.script;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptPack implements Comparable<ScriptPack>
{
	public final String id;
	public final List<ScriptFile> files;
	public final ScriptEngine engine;
	public int order;

	public ScriptPack(String i, ScriptEngine e)
	{
		id = i;
		files = new ArrayList<>();
		engine = e;
		order = 0;
	}

	@Override
	public int compareTo(ScriptPack o)
	{
		if (order != o.order)
		{
			return Integer.compare(order, o.order);
		}

		return id.compareToIgnoreCase(o.id);
	}
}