package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPHandler;
import dev.latvian.mods.kubejs.web.http.HTTPMethod;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.http.SimpleHTTPResponse;
import dev.latvian.mods.kubejs.web.ws.WSHandler;
import dev.latvian.mods.kubejs.web.ws.WSSessionFactory;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public record KubeJSLocalWebServer(HTTPServer<KJSHTTPContext> http) implements WebServerRegistry<KJSHTTPContext> {
	private static KubeJSLocalWebServer instance;

	@Nullable
	public static KubeJSLocalWebServer instance() {
		return instance;
	}

	@HideFromJS
	public static void start() {
		if (instance == null) {
			try {
				var address = Inet4Address.getByName(WebServerProperties.get().isPublic ? "0.0.0.0" : "127.0.0.1");

				var http = new HTTPServer<>(KJSHTTPContext::new);
				// var ws = new WSServer(address, ClientProperties.get().localServerWsPort);
				var s = new KubeJSLocalWebServer(http);
				KubeJSPlugins.forEachPlugin(s, KubeJSPlugin::registerLocalWebServer);
				http.setHomepageHandler(KubeJSLocalWebServer::homepage);

				if (http.start(address, WebServerProperties.get().port, Executors.newVirtualThreadPerTaskExecutor())) {
					// ws.start();
					Runtime.getRuntime().addShutdownHook(new Thread(s::stopNow, "KubeJS Web Server Shutdown Hook"));
					KubeJS.LOGGER.info("Started the local web server at http://localhost:" + http.getAddress().getPort());
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

	@Override
	public void http(HTTPMethod method, String path, HTTPHandler<KJSHTTPContext> handler) {
		http.addHandler(method, path, handler);
	}

	@Override
	public WSHandler ws(String path, WSSessionFactory factory) {
		return null;
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

	public void stopNow() {
		http.stopNow();
		KubeJS.LOGGER.info("Stopped the local web server");
	}
}
