package com.latmod.mods.worldjs.mod;

import com.latmod.mods.worldjs.command.CommandWorldJS;
import com.latmod.mods.worldjs.events.EventsJS;
import com.latmod.mods.worldjs.item.ItemStackJS;
import com.latmod.mods.worldjs.item.OreDictUtils;
import com.latmod.mods.worldjs.player.PlayerChatEventJS;
import com.latmod.mods.worldjs.player.PlayerEventJS;
import com.latmod.mods.worldjs.text.TextColor;
import com.latmod.mods.worldjs.text.TextUtils;
import com.latmod.mods.worldjs.util.ScriptFile;
import com.latmod.mods.worldjs.util.ServerJS;
import com.latmod.mods.worldjs.util.UtilsJS;
import com.latmod.mods.worldjs.world.WorldEventJS;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mod(
		modid = WorldJSMod.MOD_ID,
		name = WorldJSMod.MOD_NAME,
		version = WorldJSMod.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = "required-after:forge@[0.0.0.forge,)"
)
@Mod.EventBusSubscriber
public class WorldJSMod
{
	public static final String MOD_ID = "worldjs";
	public static final String MOD_NAME = "WorldJS";
	public static final String VERSION = "0.0.0.worldjs";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final Map<String, ScriptFile> SCRIPTS = new Object2ObjectOpenHashMap<>();
	public static final List<ITextComponent> ERRORS = new ObjectArrayList<>();
	public static ServerJS server;

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandWorldJS());
	}

	@SubscribeEvent
	public static void registerWJSEvents(WorldJSEventRegistryEvent event)
	{
		event.register("world.load", WorldEventJS.class);
		event.register("world.unload", WorldEventJS.class);
		event.register("player.logged_in", PlayerEventJS.class);
		event.register("player.logged_out", PlayerEventJS.class);
		event.register("player.chat", PlayerChatEventJS.class);
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
		loadScripts(new File(server.overworld.world.getSaveHandler().getWorldDirectory(), "scripts"), "");

		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		ClassFilter classFilter = s -> true;

		Bindings bindings = new SimpleBindings();
		MinecraftForge.EVENT_BUS.post(new WorldJSBindingsEvent(bindings::put));
		bindings.put("log", LOGGER);
		bindings.put("utils", UtilsJS.INSTANCE);
		bindings.put("server", server);
		bindings.put("events", EventsJS.INSTANCE);
		bindings.put("text", TextUtils.INSTANCE);
		bindings.put("oredict", OreDictUtils.INSTANCE);
		bindings.put("EMPTY_ITEM", ItemStackJS.EMPTY);

		for (TextColor color : TextColor.VALUES)
		{
			bindings.put(color.name(), color);
		}

		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			bindings.put("SLOT_" + slot.name(), slot);
		}

		for (ScriptFile file : SCRIPTS.values())
		{
			file.setScript(null);

			try (FileReader reader = new FileReader(file.getFile()))
			{
				NashornScriptEngine script = (NashornScriptEngine) factory.getScriptEngine(classFilter);
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

		if (file.isDirectory())
		{
			File[] files = file.listFiles();

			if (files != null)
			{
				for (File f : files)
				{
					loadScripts(f, prefix.isEmpty() ? file.getName() : (prefix + "/" + file.getName()));
				}
			}
		}
		else if (file.isFile() && file.getName().endsWith(".js"))
		{
			ScriptFile scriptFile = new ScriptFile(prefix.isEmpty() ? file.getName() : (prefix + "/" + file.getName()), file);
			LOGGER.info("Found script at " + file.getAbsolutePath());
			SCRIPTS.put(scriptFile.getPath(), scriptFile);
		}
	}
}