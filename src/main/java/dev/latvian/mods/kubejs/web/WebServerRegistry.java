package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.web.http.HTTPHandler;
import dev.latvian.mods.kubejs.web.http.HTTPMethod;

public interface WebServerRegistry {
	void http(HTTPMethod method, String path, HTTPHandler handler);

	// void ws(WebServerPath path, WSHandler handler);

	default void get(String path, HTTPHandler handler) {
		http(HTTPMethod.GET, path, handler);
	}

	default void post(String path, HTTPHandler handler) {
		http(HTTPMethod.POST, path, handler);
	}

	default void put(String path, HTTPHandler handler) {
		http(HTTPMethod.PUT, path, handler);
	}

	default void patch(String path, HTTPHandler handler) {
		http(HTTPMethod.PATCH, path, handler);
	}

	default void delete(String path, HTTPHandler handler) {
		http(HTTPMethod.DELETE, path, handler);
	}
}
