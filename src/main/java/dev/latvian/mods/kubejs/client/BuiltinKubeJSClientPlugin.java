package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.ScheduledEvents;
import dev.latvian.mods.unit.Unit;
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
		bindings.add("Painter", Painter.getGlobal());

		if (bindings.type().isClient()) {
			var se = Minecraft.getInstance().kjs$getScheduledEvents();

			bindings.add("setTimeout", new ScheduledEvents.TimeoutJSFunction(se, false, false));
			bindings.add("clearTimeout", new ScheduledEvents.TimeoutJSFunction(se, true, false));
			bindings.add("setInterval", new ScheduledEvents.TimeoutJSFunction(se, false, true));
			bindings.add("clearInterval", new ScheduledEvents.TimeoutJSFunction(se, true, true));
		}
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(Unit.class, (ctx, from) -> Painter.getGlobal().unitOf(ctx, from));
	}

	@Override
	public void generateLang(LangKubeEvent event) {
		event.add(KubeJS.MOD_ID, "key.kubejs.gui", "KubeJS (GUI)");
		event.add(KubeJS.MOD_ID, "key.kubejs.in_game", "KubeJS (In-Game)");

		if (ModList.get().isLoaded("jade")) {
			for (var mod : PlatformWrapper.getMods().values()) {
				if (!mod.getCustomName().isEmpty()) {
					event.add(KubeJS.MOD_ID, "jade.modName." + mod.getId(), mod.getCustomName());
				}
			}
		}
	}
}
