package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.http.SimpleHTTPResponse;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class KubeJSLocalWebServer extends LocalWebServer {
	@HideFromJS
	public static KubeJSLocalWebServer instance;

	@HideFromJS
	public static void start() {
		if (instance == null) {
			try {
				var s = new KubeJSLocalWebServer();
				KubeJSPlugins.forEachPlugin(s, KubeJSPlugin::registerLocalWebServer);
				s.setHomepageHandler(KubeJSLocalWebServer::homepage);

				if (s.start(ClientProperties.get().localServerHttpPort, Executors.newVirtualThreadPerTaskExecutor())) {
					Runtime.getRuntime().addShutdownHook(new Thread(s::stopNow, "KubeJS Web Server Shutdown Hook"));
					KubeJS.LOGGER.info("Started the local web server at http://localhost:" + s.server.getAddress().getPort());
					instance = s;
				} else {
					KubeJS.LOGGER.warn("Failed to start the local web server - all ports occupied");
				}
			} catch (Exception ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - error");
				ex.printStackTrace();
			}
		}
	}

	private static HTTPResponse homepage(HTTPContext ctx) {
		var list = new ArrayList<String>();
		list.add("KubeJS Local Web Server [" + Minecraft.getInstance().getGameProfile().getName() + ", " + Minecraft.getInstance().kjs$getTitle() + "]");
		list.add("");

		list.add("Loaded Plugins:");

		for (var plugin : KubeJSPlugins.getAll()) {
			list.add("- " + plugin.getClass().getName());
		}

		list.add("");
		list.add("Loaded Mods:");

		for (var mod : ModList.get().getSortedMods()) {
			list.add("- " + mod.getModInfo().getDisplayName() + " (" + mod.getModId() + " - " + mod.getModInfo().getVersion() + ")");
		}

		return SimpleHTTPResponse.text(200, list);
	}

	@Override
	public void stopNow() {
		super.stopNow();
		KubeJS.LOGGER.info("Stopped the local web server");
	}
}
