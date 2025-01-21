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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public record LocalWebServer(HTTPServer<KJSHTTPRequest> server, String url, List<Endpoint> endpoints) {
	public static String explorerCode = "";

	public record Endpoint(String method, String path, boolean auth) implements Comparable<Endpoint> {
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
	public static void start(BlockableEventLoop<?> eventLoop, boolean localClient) {
		if (instance == null) {
			try {
				var properties = WebServerProperties.get();
				var holder = new LocalWebServerRegistryHolder(eventLoop, properties.auth);
				var endpoints0 = new HashSet<LocalWebServer.Endpoint>();
				var registry = new LocalWebServerRegistry(holder, endpoints0, false);
				var registryWithAuth = /*localClient || */holder.auth.isEmpty() ? registry : new LocalWebServerRegistry(holder, endpoints0, true);

				KubeJSPlugins.forEachPlugin(registry, KubeJSPlugin::registerLocalWebServer);
				KubeJSPlugins.forEachPlugin(registryWithAuth, KubeJSPlugin::registerLocalWebServerWithAuth);
				var publicAddress = localClient ? "" : properties.publicAddress;

				if (publicAddress.startsWith("https://")) {
					publicAddress = publicAddress.substring(8);
				} else if (publicAddress.startsWith("http://")) {
					publicAddress = publicAddress.substring(7);
				}

				holder.server.setDaemon(true);
				holder.server.setServerName(KubeJS.DISPLAY_NAME);
				holder.server.setAddress(publicAddress.isEmpty() ? "127.0.0.1" : "0.0.0.0");
				holder.server.setPort(IntStream.range(properties.port, properties.port + 10));
				holder.server.setMaxKeepAliveConnections(3);
				holder.server.setKeepAliveTimeout(Duration.ofMinutes(5L));

				int port = holder.server.start();
				var url = "http://localhost:" + port;
				var endpoints = new ArrayList<>(endpoints0);
				endpoints.sort(null);
				instance = new LocalWebServer(holder.server, publicAddress.isEmpty() ? url : ("https://" + publicAddress), List.copyOf(endpoints));
				explorerCode = (publicAddress.isEmpty() ? ("p=" + port) : ("a=" + URLEncoder.encode(publicAddress, StandardCharsets.UTF_8))) + (holder.auth.isEmpty() ? "" : ("&c=" + holder.encodedAuth));

				KubeJS.LOGGER.info("Started the local web server at " + url);
			} catch (BindFailedException ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - all ports occupied");
			} catch (Exception ex) {
				KubeJS.LOGGER.warn("Failed to start the local web server - unexpected error");
				ex.printStackTrace();
			}
		}
	}
}
