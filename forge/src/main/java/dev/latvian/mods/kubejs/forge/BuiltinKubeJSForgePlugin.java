package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.level.gen.forge.BiomeDictionaryWrapper;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class BuiltinKubeJSForgePlugin extends BuiltinKubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		super.addClasses(type, filter);

		filter.allow("net.minecraftforge"); // Forge
		filter.deny("net.minecraftforge.fml");
		filter.deny("net.minecraftforge.accesstransformer");
		filter.deny("net.minecraftforge.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		super.addBindings(event);

		if (event.type == ScriptType.STARTUP) {
			event.addFunction("onForgeEvent", args -> onPlatformEvent(event, args), null, KubeJSForgeEventHandlerWrapper.class);
		}

		event.add("BiomeDictionary", BiomeDictionaryWrapper.class);
	}

	@Override
	public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		super.addTypeWrappers(type, typeWrappers);
		typeWrappers.register(BiomeDictionary.Type.class, BiomeDictionaryWrapper::getBiomeType);
	}

	public static Object onPlatformEvent(BindingsEvent event, Object[] args) {
		if (args.length < 2 || !(args[0] instanceof CharSequence)) {
			throw new RuntimeException("Invalid syntax! onPlatformEvent(string, function) required event class and handler");
		} else if (!KubeJS.startupScriptManager.firstLoad) {
			ConsoleJS.STARTUP.warn("onPlatformEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = Class.forName(args[0].toString());
			var handler = (KubeJSForgeEventHandlerWrapper) args[1];
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
