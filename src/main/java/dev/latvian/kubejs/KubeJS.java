package dev.latvian.kubejs;

import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.command.CommandKubeJS;
import dev.latvian.kubejs.events.EventsJS;
import dev.latvian.kubejs.function.EventFunction;
import dev.latvian.kubejs.function.LogFunction;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.OreDictUtils;
import dev.latvian.kubejs.text.TextColor;
import dev.latvian.kubejs.text.TextUtils;
import dev.latvian.kubejs.util.ScriptClassFilter;
import dev.latvian.kubejs.util.ScriptFile;
import dev.latvian.kubejs.util.ScriptPack;
import dev.latvian.kubejs.util.UtilsJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod(
		modid = KubeJS.MOD_ID,
		name = KubeJS.MOD_NAME,
		version = KubeJS.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = "required-after:forge@[0.0.0.forge,)"
)
public class KubeJS
{
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final String VERSION = "0.0.0.kubejs";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final Map<String, ScriptFile> SCRIPTS = new LinkedHashMap<>();
	public static JsonContext ID_CONTEXT;
	private static Map<String, ScriptPack> packs;
	public static ScriptFile currentFile;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ID_CONTEXT = new JsonContext(KubeJS.MOD_ID);
		UtilsJS.INSTANCE.init();
		packs = new HashMap<>();
		packs.put("modpack", newPack("modpack"));

		loadScripts();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		EventsJS.INSTANCE.post(KubeJSEvents.POSTINIT, new PostInitEventJS(new HashSet<>(Loader.instance().getIndexedModList().keySet())));
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandKubeJS());
	}

	private static ScriptPack newPack(String id)
	{
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		//ScriptEngine engine = factory.getScriptEngine(new String[] {"-doe"}, KubeJS.class.getClassLoader(), ScriptClassFilter.INSTANCE);
		ScriptEngine engine = factory.getScriptEngine(ScriptClassFilter.INSTANCE);
		ScriptContext context = engine.getContext();

		for (String s : ScriptClassFilter.BLOCKED_FUNCTIONS)
		{
			context.removeAttribute(s, context.getAttributesScope(s));
		}

		return new ScriptPack(id, engine);
	}

	public static long loadScripts()
	{
		long now = System.currentTimeMillis();

		if (!SCRIPTS.isEmpty())
		{
			//Some kind of unload event?
			SCRIPTS.clear();
		}

		EventsJS.INSTANCE.clear();

		List<ScriptFile> scriptFiles = new ArrayList<>();
		loadScripts(scriptFiles, new File(Loader.instance().getConfigDir().getParentFile(), "kubejs"), "", 0);
		scriptFiles.sort(null);

		for (ScriptFile file : scriptFiles)
		{
			LOGGER.info("Found script at " + file.path);
			SCRIPTS.put(file.path, file);
		}

		Bindings bindings = new SimpleBindings();
		MinecraftForge.EVENT_BUS.post(new KubeJSBindingsEvent(bindings::put));

		bindings.put("utils", UtilsJS.INSTANCE);
		bindings.put("events", EventsJS.INSTANCE);
		bindings.put("text", TextUtils.INSTANCE);
		bindings.put("oredict", OreDictUtils.INSTANCE);
		bindings.put("material", MaterialListJS.INSTANCE);

		bindings.put("EMPTY_ITEM", ItemStackJS.EMPTY);
		bindings.put("SECOND", 1000L);
		bindings.put("MINUTE", 60000L);
		bindings.put("HOUR", 3600000L);

		for (TextColor color : TextColor.VALUES)
		{
			bindings.put(color.name(), color);
		}

		bindings.put("SLOT_MAINHAND", EntityEquipmentSlot.MAINHAND.ordinal());
		bindings.put("SLOT_OFFHAND", EntityEquipmentSlot.OFFHAND.ordinal());
		bindings.put("SLOT_FEET", EntityEquipmentSlot.FEET.ordinal());
		bindings.put("SLOT_LEGS", EntityEquipmentSlot.LEGS.ordinal());
		bindings.put("SLOT_CHEST", EntityEquipmentSlot.CHEST.ordinal());
		bindings.put("SLOT_HEAD", EntityEquipmentSlot.HEAD.ordinal());

		bindings.put("onEvent", (EventFunction) EventsJS.INSTANCE::listen);
		bindings.put("info", (LogFunction) (text, objects) -> {LOGGER.info(objects.length == 0 ? text : String.format(text, objects));});
		bindings.put("warn", (LogFunction) (text, objects) -> {LOGGER.warn(objects.length == 0 ? text : String.format(text, objects));});
		bindings.put("error", (LogFunction) (text, objects) -> {LOGGER.error(objects.length == 0 ? text : String.format(text, objects));});

		int i = 0;

		for (ScriptFile file : SCRIPTS.values())
		{
			currentFile = file;

			if (file.load(bindings))
			{
				i++;
			}
		}

		currentFile = null;

		for (ScriptFile file : SCRIPTS.values())
		{
			if (file.getError() != null)
			{
				LOGGER.error("Error loading KubeJS script " + file.path + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));

				if (!(file.getError() instanceof ScriptException))
				{
					file.getError().printStackTrace();
				}
			}
		}

		long time = System.currentTimeMillis() - now;
		LOGGER.info("Loaded " + i + "/" + SCRIPTS.size() + " scripts in " + (time / 1000D) + "s");
		return time;
	}

	private static void loadScripts(List<ScriptFile> scriptFiles, File file, String prefix, int depth)
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
					loadScripts(scriptFiles, f, p, depth + 1);
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
					LOGGER.warn("Packaged scripts in " + p + " are not supported yet!");
				}
			}
		}
	}
}