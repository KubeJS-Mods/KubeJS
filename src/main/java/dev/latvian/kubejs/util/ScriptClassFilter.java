package dev.latvian.kubejs.util;

import com.google.common.collect.ListMultimap;
import jdk.nashorn.api.scripting.ClassFilter;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private Set<String> modPackages;

	private ScriptClassFilter()
	{
		whitelist = new LinkedList<>();
		modPackages = null;

		whitelist("java.lang");
		whitelist("java.util");
		whitelist("dev.latvian.kubejs");
	}

	public void whitelist(String name)
	{
		whitelist.add(name);
	}

	@Override
	@SuppressWarnings("deprecation")
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

		if (modPackages == null)
		{
			try
			{
				LoadController instance = ReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "modController");
				ListMultimap<String, ModContainer> packageOwners = ReflectionHelper.getPrivateValue(LoadController.class, instance, "packageOwners");
				modPackages = new HashSet<>(packageOwners.keySet());
			}
			catch (Exception ex)
			{
				modPackages = Collections.emptySet();
			}
		}

		if (modPackages.isEmpty())
		{
			return false;
		}

		String pkg = n.substring(0, packageIndex);
		return modPackages.contains(pkg);
	}
}
