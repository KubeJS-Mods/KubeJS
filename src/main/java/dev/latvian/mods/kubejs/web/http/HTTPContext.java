package dev.latvian.mods.kubejs.web.http;

import com.sun.net.httpserver.HttpExchange;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record HTTPContext(Map<String, String> variables, Map<String, String> query, String[] path) {
	public static HTTPContext of(LocalWebServer.PathHandler handler, HttpExchange exchange, String[] path) {
		var query = exchange.getRequestURI().getQuery();
		var variableMap = handler.path().variables() == 0 ? Map.<String, String>of() : new HashMap<String, String>(handler.path().variables());
		var queryMap = query == null ? Map.<String, String>of() : new HashMap<String, String>(2);

		if (handler.path().variables() > 0) {
			for (var i = 0; i < handler.path().parts().length; i++) {
				var part = handler.path().parts()[i];

				if (part.variable()) {
					variableMap.put(part.name(), path[i]);
				}
			}
		}

		if (query != null) {
			for (String param : query.split("&")) {
				String[] entry = param.split("=", 2);
				if (entry.length > 1) {
					queryMap.put(entry[0], entry[1]);
				} else {
					queryMap.put(entry[0], "");
				}
			}
		}

		return new HTTPContext(variableMap, queryMap, path);
	}

	public void runInRenderThread(Runnable task) {
		Minecraft.getInstance().executeBlocking(task);
	}

	public <T> T supplyInRenderThread(Supplier<T> task) {
		return CompletableFuture.supplyAsync(task, Minecraft.getInstance()).join();
	}

	public ResourceLocation id() {
		return ResourceLocation.fromNamespaceAndPath(variables.get("namespace"), variables.get("path"));
	}
}
