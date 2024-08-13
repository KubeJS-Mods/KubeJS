package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.error.BindFailedException;
import dev.latvian.apps.tinyserver.http.HTTPHandler;
import dev.latvian.apps.tinyserver.http.HTTPMethod;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.apps.tinyserver.ws.WSSessionFactory;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public record KubeJSLocalWebServer(HTTPServer<KJSHTTPRequest> server) implements ServerRegistry<KJSHTTPRequest> {
	private static KubeJSLocalWebServer instance;

	@Nullable
	public static KubeJSLocalWebServer instance() {
		return instance;
	}

	@HideFromJS
	public static void start() {
		if (instance == null) {
			try {
				var server = new HTTPServer<>(KJSHTTPRequest::new);
				// var ws = new WSServer(address, ClientProperties.get().localServerWsPort);
				var s = new KubeJSLocalWebServer(server);
				KubeJSPlugins.forEachPlugin(s, KubeJSPlugin::registerLocalWebServer);
				server.get("/", KubeJSLocalWebServer::homepage);

				server.setDaemon(true);
				server.setServerName("KubeJS " + KubeJS.VERSION);
				server.setAddress(WebServerProperties.get().publicAddress.isEmpty() ? "127.0.0.1" : "0.0.0.0");
				server.setPort(WebServerProperties.get().port);
				server.setMaxPortShift(10);

				KubeJS.LOGGER.info("Started the local web server at http://localhost:" + server.start());
				instance = s;
			} catch (BindFailedException ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - all ports occupied");
			} catch (Exception ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - error");
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void http(HTTPMethod httpMethod, String s, HTTPHandler<KJSHTTPRequest> httpHandler) {
		server.http(httpMethod, s, httpHandler);
	}

	@Override
	public <WSS extends WSSession<KJSHTTPRequest>> WSHandler<KJSHTTPRequest, WSS> ws(String s, WSSessionFactory<KJSHTTPRequest, WSS> wsSessionFactory) {
		return server.ws(s, wsSessionFactory);
	}

	private static HTTPResponse homepage(KJSHTTPRequest ctx) {
		var list = new ArrayList<String>();
		list.add("KubeJS Local Web Server [" + KubeJS.PROXY.getWebServerWindowTitle() + "]");
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

		return HTTPResponse.ok().text(list);
	}

	public void stopNow() {
		server.stop();
		KubeJS.LOGGER.info("Stopped the local web server");
	}
}
