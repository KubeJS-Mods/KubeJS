package dev.latvian.mods.kubejs.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPHandler;
import dev.latvian.mods.kubejs.web.http.HTTPMethod;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class HTTPServer<CTX extends HTTPContext> implements HttpHandler {
	public record PathHandler<CTX extends HTTPContext>(HTTPMethod method, CompiledPath path, HTTPHandler<CTX> handler) {
	}

	private final Supplier<CTX> contextFactory;
	private final Map<HTTPMethod, List<PathHandler<CTX>>> handlers;
	private HttpServer server;
	private PathHandler<CTX> homepageHandler;

	public HTTPServer(Supplier<CTX> contextFactory) {
		this.contextFactory = contextFactory;
		this.handlers = new EnumMap<>(HTTPMethod.class);
	}

	public void setHomepageHandler(HTTPHandler<CTX> handler) {
		homepageHandler = new PathHandler<>(HTTPMethod.GET, CompiledPath.EMPTY, handler);
	}

	public boolean start(InetAddress address, int port, @Nullable Executor executor) {
		int portOffset = 0;

		while (true) {
			try {
				server = HttpServer.create(new InetSocketAddress(address, port + portOffset), 0);
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

	public InetSocketAddress getAddress() {
		return server.getAddress();
	}

	public void addHandler(HTTPMethod method, String path, HTTPHandler<CTX> handler) {
		var compiledPath = CompiledPath.compile(path);
		handlers.computeIfAbsent(method, k -> new ArrayList<>()).add(new PathHandler<>(method, compiledPath, handler));
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
						var ctx = contextFactory.get();
						ctx.init(homepageHandler.path, exchange);
						homepageHandler.handler().handle(ctx).respond(exchange);
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
					var ctx = contextFactory.get();
					ctx.setPath(p);
					ctx.setBody(streamLazy(exchange.getRequestBody()));
					ctx.init(handler.path, exchange);
					handler.handler().handle(ctx).respond(exchange);
					return;
				}
			}

			exchange.sendResponseHeaders(404, -1L);
		} catch (Exception ex) {
			ex.printStackTrace();
			exchange.sendResponseHeaders(500, -1L);
		}
	}

	private static Lazy<String> streamLazy(InputStream stream) {
		return Lazy.of(() -> {
			try {
				return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception ex) {
				return "";
			}
		});
	}

	public void stopNow() {
		if (server != null) {
			server.stop(0);
		}
	}
}
