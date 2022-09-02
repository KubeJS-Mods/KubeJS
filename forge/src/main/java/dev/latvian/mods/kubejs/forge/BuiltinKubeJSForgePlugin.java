package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.integration.forge.jei.JEIKubeJSEvents;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;

public class BuiltinKubeJSForgePlugin extends BuiltinKubeJSPlugin {
	@Override
	public void registerEvents() {
		super.registerEvents();
		ForgeKubeJSEvents.register();

		if (ModList.get().isLoaded("jei")) {
			JEIKubeJSEvents.register();
		}
	}

	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		super.registerClasses(type, filter);

		filter.allow("net.minecraftforge"); // Forge
		filter.deny("net.minecraftforge.fml");
		filter.deny("net.minecraftforge.accesstransformer");
		filter.deny("net.minecraftforge.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		super.registerBindings(event);

		if (event.type == ScriptType.STARTUP) {
			event.addFunction("onForgeEvent", BuiltinKubeJSForgePlugin::onForgeEvent, null, KubeJSForgeEventHandlerWrapper.class);
		}
	}

	public static Object onForgeEvent(Object[] args) {
		if (args.length < 2 || !(args[0] instanceof CharSequence || args[0] instanceof Class)) {
			throw new RuntimeException("Invalid syntax! onForgeEvent(eventType, function) requires event class and handler");
		} else if (!KubeJS.getStartupScriptManager().firstLoad) {
			ConsoleJS.STARTUP.warn("onForgeEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = args[0] instanceof Class<?> c ? c : Class.forName(args[0].toString());
			var handler = (KubeJSForgeEventHandlerWrapper) args[1];
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
