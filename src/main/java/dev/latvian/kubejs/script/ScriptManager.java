package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.util.UtilsJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private static final String[] BLOCKED_FUNCTIONS = {
			"print",
			"load",
			"loadWithNewGlobal",
			"exit",
			"quit"
	};

	public final ScriptType type;
	public final Path directory;
	public final String exampleScript;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	public final List<String> errors;

	public ScriptFile currentFile;
	public Map<String, Object> bindings, constants;

	public ScriptManager(ScriptType t, Path p, String e)
	{
		type = t;
		directory = p;
		exampleScript = e;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		errors = new ArrayList<>();
	}

	public void unload()
	{
		events.clear();
		packs.clear();
	}

	public void loadFromDirectory()
	{
		if (Files.notExists(directory))
		{
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (InputStream in = KubeJS.class.getResourceAsStream(exampleScript);
				 OutputStream out = Files.newOutputStream(directory.resolve("script.js")))
			{
				out.write(IOUtils.toByteArray(in));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (ScriptFileInfo fileInfo : pack.info.scripts)
		{
			ScriptSource.FromPath scriptSource = info -> directory.resolve(info.file);

			Throwable error = fileInfo.preload(scriptSource);

			if (error == null)
			{
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			}
			else
			{
				KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public void load()
	{
		BabelExecutor.init();

		long startAll = System.currentTimeMillis();

		errors.clear();
		bindings = new HashMap<>();
		constants = new HashMap<>();
		BindingsEvent event = new BindingsEvent(type, bindings, constants);
		MinecraftForge.EVENT_BUS.post(event);
		DefaultBindings.init(this, event);
		Bindings b = new SimpleBindings();
		b.putAll(constants);
		b.putAll(bindings);

		int i = 0;
		int t = 0;

		for (ScriptPack pack : packs.values())
		{
			pack.engine = new NashornScriptEngineFactory().getScriptEngine(s -> false);

			ScriptContext context = pack.engine.getContext();

			for (String s : BLOCKED_FUNCTIONS)
			{
				context.removeAttribute(s, context.getAttributesScope(s));
			}

			for (ScriptFile file : pack.scripts)
			{
				t++;
				currentFile = file;
				long start = System.currentTimeMillis();

				if (file.load(b))
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

		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new EventJS());
		MinecraftForge.EVENT_BUS.post(new ScriptsLoadedEvent());

		if (i != t && type == ScriptType.STARTUP)
		{
			throw new RuntimeException("There were startup script errors! See latest.log for more info");
		}
	}
}