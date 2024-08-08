package dev.latvian.mods.kubejs.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPHandler;
import dev.latvian.mods.kubejs.web.http.HTTPMethod;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class LocalWebServer implements WebServerRegistry, HttpHandler {
	public record PathHandler(HTTPMethod method, CompiledPath path, HTTPHandler handler) {
	}

	public HttpServer server;
	private final Map<HTTPMethod, List<PathHandler>> handlers;
	private PathHandler homepageHandler;

	public LocalWebServer() {
		this.handlers = new EnumMap<>(HTTPMethod.class);
	}

	public void setHomepageHandler(HTTPHandler handler) {
		homepageHandler = new PathHandler(HTTPMethod.GET, CompiledPath.EMPTY, handler);
	}

	public boolean start(int port, @Nullable Executor executor) {
		int portOffset = 0;

		while (true) {
			try {
				server = HttpServer.create(new InetSocketAddress(port + portOffset), 0);
				break;
			} catch (IOException ex) {
				portOffset++;

				if (portOffset >= 10) {
					return false;
				}
			}
		}

		server.createContext("/", this);
		server.setExecutor(executor);
		server.start();
		return true;
	}

	@Override
	public void http(HTTPMethod method, String path, HTTPHandler handler) {
		var compiledPath = CompiledPath.compile(path);
		handlers.computeIfAbsent(method, k -> new ArrayList<>()).add(new PathHandler(method, compiledPath, handler));
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			var method = HTTPMethod.fromString(exchange.getRequestMethod());
			var path = exchange.getRequestURI().getPath();

			while (path.startsWith("/")) {
				path = path.substring(1);
			}

			while (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}

			if (path.isBlank()) {
				if (method == HTTPMethod.GET) {
					if (homepageHandler != null) {
						homepageHandler.handler().handle(HTTPContext.of(homepageHandler, exchange, RegistryAccessContainer.current, new String[0])).respond(exchange);
					} else {
						HTTPResponse.OK.respond(exchange);
					}
				} else {
					exchange.sendResponseHeaders(404, -1L);
				}

				return;
			}

			var pathParts = path.split("/");

			for (var handler : handlers.getOrDefault(method, List.of())) {
				var p = handler.path().matches(pathParts);

				if (p != null) {
					handler.handler().handle(HTTPContext.of(handler, exchange, RegistryAccessContainer.current, p)).respond(exchange);
					return;
				}
			}

			exchange.sendResponseHeaders(404, -1L);
		} catch (Exception ex) {
			ex.printStackTrace();
			exchange.sendResponseHeaders(500, -1L);
		}
	}

	public void stopNow() {
		if (server != null) {
			server.stop(0);
		}
	}
}
