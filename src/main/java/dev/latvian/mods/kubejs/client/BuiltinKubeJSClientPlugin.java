package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.kubejs.web.WebServerRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

public class BuiltinKubeJSClientPlugin implements KubeJSPlugin {
	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(ClientEvents.GROUP);
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("Client", Minecraft.getInstance());

		if (bindings.type().isClient()) {
			var se = Minecraft.getInstance().kjs$getScheduledEvents();

			bindings.add("setTimeout", new ScheduledEvents.TimeoutJSFunction(se, false, false));
			bindings.add("clearTimeout", new ScheduledEvents.TimeoutJSFunction(se, true, false));
			bindings.add("setInterval", new ScheduledEvents.TimeoutJSFunction(se, false, true));
			bindings.add("clearInterval", new ScheduledEvents.TimeoutJSFunction(se, true, true));
		}
	}

	@Override
	public void registerLocalWebServer(WebServerRegistry registry) {
		registry.get("img/{size}/item/{namespace}/<path>", ImageGenerator::item);
		registry.get("img/{size}/block/{namespace}/<path>", ImageGenerator::block);
		registry.get("img/{size}/fluid/{namespace}/<path>", ImageGenerator::fluid);
		registry.get("img/{size}/item-tag/{namespace}/<path>", ImageGenerator::itemTag);
		registry.get("img/{size}/block-tag/{namespace}/<path>", ImageGenerator::blockTag);
		registry.get("img/{size}/fluid-tag/{namespace}/<path>", ImageGenerator::fluidTag);
	}

	@Override
	public void generateLang(LangKubeEvent event) {
		event.add(KubeJS.MOD_ID, "key.categories.kubejs", "KubeJS");
		event.add(KubeJS.MOD_ID, "key.kubejs.kubedex", "Kubedex");

		if (ModList.get().isLoaded("jade")) {
			for (var mod : PlatformWrapper.getMods().values()) {
				if (!mod.getCustomName().isEmpty()) {
					event.add(KubeJS.MOD_ID, "jade.modName." + mod.getId(), mod.getCustomName());
				}
			}
		}
	}
}
