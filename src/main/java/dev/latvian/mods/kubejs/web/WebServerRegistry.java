package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.web.http.HTTPContext;
import dev.latvian.mods.kubejs.web.http.HTTPHandler;
import dev.latvian.mods.kubejs.web.http.HTTPMethod;
import dev.latvian.mods.kubejs.web.http.HTTPResponse;
import dev.latvian.mods.kubejs.web.ws.WSHandler;
import dev.latvian.mods.kubejs.web.ws.WSSession;
import dev.latvian.mods.kubejs.web.ws.WSSessionFactory;

import java.util.function.Consumer;

public interface WebServerRegistry<CTX extends HTTPContext> {
	void http(HTTPMethod method, String path, HTTPHandler<CTX> handler);

	WSHandler ws(String path, WSSessionFactory factory);

	default WSHandler ws(String path) {
		return ws(path, WSSession::new);
	}

	default void get(String path, HTTPHandler<CTX> handler) {
		http(HTTPMethod.GET, path, handler);
	}

	default void post(String path, HTTPHandler<CTX> handler) {
		http(HTTPMethod.POST, path, handler);
	}

	default void acceptPostTask(String path, Runnable task) {
		post(path, ctx -> {
			task.run();
			return HTTPResponse.NO_CONTENT;
		});
	}

	default void acceptPostString(String path, Consumer<String> consumer) {
		post(path, ctx -> {
			consumer.accept(ctx.body());
			return HTTPResponse.NO_CONTENT;
		});
	}

	default void put(String path, HTTPHandler<CTX> handler) {
		http(HTTPMethod.PUT, path, handler);
	}

	default void patch(String path, HTTPHandler<CTX> handler) {
		http(HTTPMethod.PATCH, path, handler);
	}

	default void delete(String path, HTTPHandler<CTX> handler) {
		http(HTTPMethod.DELETE, path, handler);
	}
}
