package com.latmod.mods.kubejs;

import com.latmod.mods.kubejs.command.CommandKubeJS;
import com.latmod.mods.kubejs.events.EventsJS;
import com.latmod.mods.kubejs.item.ItemStackJS;
import com.latmod.mods.kubejs.item.OreDictUtils;
import com.latmod.mods.kubejs.text.TextColor;
import com.latmod.mods.kubejs.text.TextUtils;
import com.latmod.mods.kubejs.util.ScriptFile;
import com.latmod.mods.kubejs.util.ServerJS;
import com.latmod.mods.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
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
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
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
	public static final Map<String, ScriptFile> SCRIPTS = new Object2ObjectOpenHashMap<>();
	public static final List<ITextComponent> ERRORS = new ObjectArrayList<>();
	public static JsonContext ID_CONTEXT;
	public static ServerJS server;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ID_CONTEXT = new JsonContext(KubeJS.MOD_ID);
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

	public static void loadScripts()
	{
		long now = System.currentTimeMillis();

		if (!SCRIPTS.isEmpty())
		{
			SCRIPTS.clear();
		}

		ERRORS.clear();
		EventsJS.INSTANCE.clear();

		loadScripts(new File(Loader.instance().getConfigDir().getParentFile(), "scripts"), "");

		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		ClassFilter classFilter = s -> true; //TODO: Improve this

		Bindings bindings = new SimpleBindings();
		MinecraftForge.EVENT_BUS.post(new KubeJSBindingsEvent(bindings::put));
		bindings.put("log", LOGGER);
		bindings.put("utils", UtilsJS.INSTANCE);
		bindings.put("server", server);
		bindings.put("events", EventsJS.INSTANCE);
		bindings.put("text", TextUtils.INSTANCE);
		bindings.put("oredict", OreDictUtils.INSTANCE);
		bindings.put("EMPTY_ITEM", ItemStackJS.EMPTY);
		bindings.put("SECOND", 1000L);
		bindings.put("MINUTE", 60000L);
		bindings.put("HOUR", 3600000L);

		for (TextColor color : TextColor.VALUES)
		{
			bindings.put(color.name(), color);
		}

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			bindings.put("SLOT_" + slot.getName().toUpperCase(), slot);
		}

		for (ScriptFile file : SCRIPTS.values())
		{
			file.setScript(null);

			try (FileReader reader = new FileReader(file.getFile()))
			{
				NashornScriptEngine script = (NashornScriptEngine) factory.getScriptEngine(classFilter);
				Bindings b = script.getBindings(ScriptContext.ENGINE_SCOPE);
				b.remove("print");
				b.remove("load");
				b.remove("loadWithNewGlobal");
				b.remove("exit");
				b.remove("quit");
				script.getContext().setBindings(b, ScriptContext.ENGINE_SCOPE);
				script.getContext().setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
				script.eval(reader);
				file.setScript(script);
			}
			catch (Exception ex)
			{
				ITextComponent errorc = new TextComponentString("");
				errorc.appendSibling(new TextComponentString("Error loading WorldJS script "));
				errorc.getStyle().setColor(TextFormatting.RED);
				errorc.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ex.toString())));
				ITextComponent pathc = new TextComponentString(file.getPath());
				pathc.getStyle().setColor(TextFormatting.GOLD);
				pathc.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(file.getFile().getAbsolutePath())));
				ERRORS.add(new TextComponentString("").appendSibling(errorc).appendSibling(pathc));
				LOGGER.error("Error loading WorldJS script " + file.getFile().getAbsolutePath() + ": " + ex);
			}
		}

		LOGGER.info("Loaded " + SCRIPTS.size() + " scripts in " + ((System.currentTimeMillis() - now) / 1000D) + "s");
	}

	private static void loadScripts(File file, String prefix)
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
					loadScripts(f, p);
				}
			}
		}
		else if (file.isFile() && file.getName().endsWith(".js"))
		{
			ScriptFile scriptFile = new ScriptFile(p, file);
			LOGGER.info("Found script at " + file.getAbsolutePath());
			SCRIPTS.put(scriptFile.getPath(), scriptFile);
		}
	}
}