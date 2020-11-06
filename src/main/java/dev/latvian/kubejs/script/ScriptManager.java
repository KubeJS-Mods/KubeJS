package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import net.minecraftforge.common.MinecraftForge;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ScriptManager
{
	public final ScriptType type;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	public final List<String> errors;

	public ScriptFile currentFile;
	public Map<String, Object> bindings, constants;

	public ScriptManager(ScriptType t)
	{
		type = t;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		errors = new ArrayList<>();
	}

	public void unload()
	{
		events.clear();
		packs.clear();
	}

	public void load()
	{
		Context context = Context.enter();
		context.setLanguageVersion(Context.VERSION_ES6);
		context.setClassShutter((fullClassName, type) -> type != ClassShutter.TYPE_CLASS_IN_PACKAGE);

		long startAll = System.currentTimeMillis();

		errors.clear();

		int i = 0;
		int t = 0;

		for (ScriptPack pack : packs.values())
		{
			pack.context = context;
			pack.scope = context.initStandardObjects();

			BindingsEvent event = new BindingsEvent(type, pack.scope);
			MinecraftForge.EVENT_BUS.post(event);
			DefaultBindings.init(this, event);

			for (ScriptFile file : pack.scripts)
			{
				t++;
				currentFile = file;
				long start = System.currentTimeMillis();

				if (file.load())
				{
					i++;
					type.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
				}
				else if (file.getError() != null)
				{
					type.console.error("Error loading KubeJS script " + file.info.location + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));

					if (!(file.getError() instanceof ScriptException))
					{
						file.getError().printStackTrace();
					}

					errors.add(file.info.location + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));
				}
			}
		}

		currentFile = null;

		if (i == t)
		{
			type.console.info("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		}
		else
		{
			type.console.error("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		}

		Context.exit();

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new EventJS());
		MinecraftForge.EVENT_BUS.post(new ScriptsLoadedEvent());
	}
}