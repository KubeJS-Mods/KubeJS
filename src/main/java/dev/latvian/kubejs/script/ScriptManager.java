package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSBindingsEvent;
import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.event.EventsJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ScriptManager
{
	public static ScriptManager instance;

	public final Map<String, ScriptFile> scripts;
	private final Map<String, ScriptPack> packs;
	public ScriptFile currentFile;

	public ScriptManager()
	{
		scripts = new LinkedHashMap<>();
		packs = new HashMap<>();
		packs.put("modpack", newPack("modpack"));
	}

	private ScriptPack newPack(String id)
	{
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine(ScriptClassFilter.INSTANCE);
		ScriptContext context = engine.getContext();

		for (String s : ScriptClassFilter.BLOCKED_FUNCTIONS)
		{
			context.removeAttribute(s, context.getAttributesScope(s));
		}

		return new ScriptPack(id, engine);
	}

	public long load()
	{
		long now = System.currentTimeMillis();

		if (!scripts.isEmpty())
		{
			//Some kind of unload event?
			scripts.clear();
		}

		EventsJS.clear();

		List<ScriptFile> scriptFiles = new ArrayList<>();
		load(scriptFiles, new File(Loader.instance().getConfigDir().getParentFile(), "kubejs"), "", 0);
		scriptFiles.sort(null);

		for (ScriptFile file : scriptFiles)
		{
			KubeJS.LOGGER.info("Found script at " + file.path);
			scripts.put(file.path, file);
		}

		Bindings bindings = new SimpleBindings();
		KubeJSBindingsEvent event = new KubeJSBindingsEvent(bindings);
		MinecraftForge.EVENT_BUS.post(event);
		DefaultBindings.init(event);

		int i = 0;

		for (ScriptFile file : scripts.values())
		{
			currentFile = file;

			if (file.load(bindings))
			{
				i++;
			}
		}

		currentFile = null;

		for (ScriptFile file : scripts.values())
		{
			if (file.getError() != null)
			{
				KubeJS.LOGGER.error("Error loading KubeJS script " + file.path + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));

				if (!(file.getError() instanceof ScriptException))
				{
					file.getError().printStackTrace();
				}
			}
		}

		Documentation.INSTANCE.init();
		long time = System.currentTimeMillis() - now;
		KubeJS.LOGGER.info("Loaded " + i + "/" + scripts.size() + " scripts in " + (time / 1000D) + "s");
		return time;
	}

	private void load(List<ScriptFile> scriptFiles, File file, String prefix, int depth)
	{
		if (!file.exists())
		{
			return;
		}

		String p = prefix.isEmpty() ? file.getName() : (prefix + "/" + file.getName());

		if (file.isDirectory())
		{
			File[] files = file.listFiles();

			if (files != null)
			{
				for (File f : files)
				{
					load(scriptFiles, f, p, depth + 1);
				}
			}
		}
		else if (file.isFile())
		{
			int e = file.getName().lastIndexOf('.');

			if (e != -1)
			{
				String ext = file.getName().substring(e + 1);

				if (ext.equals("js"))
				{
					int d = depth;

					if (file.getName().equals("init.js"))
					{
						d -= 100;
					}

					scriptFiles.add(new ScriptFile(packs.get("modpack"), p, d, () -> new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
				}
				else if (ext.equals("jar") || ext.equals("zip"))
				{
					KubeJS.LOGGER.warn("Packaged scripts in " + p + " are not supported yet!");
				}
			}
		}
	}
}