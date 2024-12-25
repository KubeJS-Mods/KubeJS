package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import dev.latvian.apps.tinyserver.error.BindFailedException;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public record LocalWebServer(HTTPServer<KJSHTTPRequest> server, String url, List<Endpoint> endpoints) {
	public static final String SERVER_NAME = "KubeJS " + KubeJS.VERSION;

	public record Endpoint(String method, String path) implements Comparable<Endpoint> {
		@Override
		public int compareTo(@NotNull LocalWebServer.Endpoint o) {
			return path.compareToIgnoreCase(o.path);
		}
	}

	private static LocalWebServer instance;

	@Nullable
	public static LocalWebServer instance() {
		return instance;
	}

	@HideFromJS
	public static void start(BlockableEventLoop<?> eventLoop) {
		if (instance == null) {
			try {
				var registry = new LocalWebServerRegistry(eventLoop);
				KubeJSPlugins.forEachPlugin(registry, KubeJSPlugin::registerLocalWebServer);
				var publicAddress = WebServerProperties.get().publicAddress;

				registry.server.setDaemon(true);
				registry.server.setServerName(SERVER_NAME);
				registry.server.setAddress(publicAddress.isEmpty() ? "127.0.0.1" : "0.0.0.0");
				registry.server.setPort(WebServerProperties.get().port);
				registry.server.setMaxPortShift(10);
				registry.server.setMaxKeepAliveConnections(3);
				registry.server.setKeepAliveTimeout(Duration.ofMinutes(5L));

				int port = registry.server.start();
				var url = "http://localhost:" + port;
				KubeJS.LOGGER.info("Started the local web server at " + url);
				var endpoints = new ArrayList<>(registry.endpoints);
				endpoints.sort(null);
				instance = new LocalWebServer(registry.server, publicAddress.isEmpty() ? url : publicAddress, List.copyOf(endpoints));
			} catch (BindFailedException ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - all ports occupied");
			} catch (Exception ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - unexpected error");
				ex.printStackTrace();
			}
		}
	}
}
