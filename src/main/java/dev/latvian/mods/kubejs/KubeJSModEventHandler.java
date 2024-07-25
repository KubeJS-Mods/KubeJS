package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabCallbackForge;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabKubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

@EventBusSubscriber(modid = KubeJS.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KubeJSModEventHandler {
	/*
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void commonSetup(FMLCommonSetupEvent event) {
	}
	 */

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
		var tabId = event.getTabKey().location();

		if (StartupEvents.MODIFY_CREATIVE_TAB.hasListeners(tabId)) {
			StartupEvents.MODIFY_CREATIVE_TAB.post(ScriptType.STARTUP, tabId, new CreativeTabKubeEvent(event.getTab(), event.hasPermissions(), new CreativeTabCallbackForge(event)));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(KubeJSModEventHandler::loadComplete0);
	}

	private static void loadComplete0() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::afterInit);
		NeoForge.EVENT_BUS.post(new ScriptsLoadedEvent());
		StartupEvents.POST_INIT.post(ScriptType.STARTUP, KubeStartupEvent.BASIC);
		UtilsJS.postModificationEvents();

		if (!ConsoleJS.STARTUP.errors.isEmpty()) {
			var list = new ArrayList<String>();
			list.add("Startup script errors:");

			var lines = ConsoleJS.STARTUP.errors.toArray(ConsoleLine.EMPTY_ARRAY);

			for (int i = 0; i < lines.length; i++) {
				list.add((i + 1) + ") " + lines[i]);
			}

			KubeJS.LOGGER.error(String.join("\n", list));

			ConsoleJS.STARTUP.flush(true);

			if (FMLLoader.getDist() == Dist.DEDICATED_SERVER || !CommonProperties.get().startupErrorGUI) {
				throw new RuntimeException("There were KubeJS startup script syntax errors! See logs/kubejs/startup.log for more info");
			}
		}

		ConsoleJS.STARTUP.stopCapturingErrors();
		ConsoleJS.CLIENT.stopCapturingErrors();

		Util.nonCriticalIoPool().submit(() -> {
			try {
				var response = HttpClient.newBuilder()
					.followRedirects(HttpClient.Redirect.ALWAYS)
					.connectTimeout(Duration.ofSeconds(5L))
					.build()
					.send(HttpRequest.newBuilder().uri(URI.create("https://v.kubejs.com/update-check?" + KubeJS.QUERY)).GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
				if (response.statusCode() == 200) {
					var body = response.body().trim();

					if (!body.isEmpty()) {
						ConsoleJS.STARTUP.info("Update available: " + body);
					}
				}
			} catch (Exception ignored) {
			}
		});
	}

	/*
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addPacksFirst(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			// ServerScriptManager.addPacksFirst(event);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void addPacksLast(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			// ServerScriptManager.addPacksLast(event);
		}
	}

	 */
}
