package dev.latvian.kubejs.util;

import jdk.nashorn.api.scripting.ClassFilter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ScriptClassFilter implements ClassFilter
{
	public static final String[] BLOCKED_FUNCTIONS = {
			"print",
			"load",
			"loadWithNewGlobal",
			"exit",
			"quit"
	};

	public static final ScriptClassFilter INSTANCE = new ScriptClassFilter();

	private final List<String> whitelist;

	private ScriptClassFilter()
	{
		whitelist = new LinkedList<>();
		whitelist.add("java.lang");
		whitelist.add("java.util");
	}

	@Override
	public boolean exposeToScripts(String name)
	{
		String n = name.replace('$', '.');
		int packageIndex = n.lastIndexOf('.');

		if (packageIndex == -1)
		{
			return false;
		}

		for (String s : whitelist)
		{
			if (n.startsWith(s))
			{
				return true;
			}
		}

		return false;
	}
}
