package dev.latvian.mods.kubejs.web.http;

import com.sun.net.httpserver.HttpExchange;
import dev.latvian.mods.kubejs.web.CompiledPath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HTTPContext {
	public static final Supplier<String> NO_BODY = () -> "";

	private String[] path = new String[0];
	private Map<String, String> variables = Map.of();
	private Map<String, String> query = Map.of();
	private Supplier<String> body = NO_BODY;

	public void setPath(String[] path) {
		this.path = path;
	}

	public void setBody(Supplier<String> body) {
		this.body = body;
	}

	public void init(CompiledPath compiledPath, HttpExchange exchange) {
		if (compiledPath.variables() > 0) {
			this.variables = new HashMap<>(compiledPath.variables());

			for (var i = 0; i < compiledPath.parts().length; i++) {
				var part = compiledPath.parts()[i];

				if (part.variable()) {
					variables.put(part.name(), path[i]);
				}
			}
		}

		var queryStr = exchange.getRequestURI().getQuery();

		if (queryStr != null) {
			this.query = new HashMap<>(2);

			for (String param : queryStr.split("&")) {
				var entry = param.split("=", 2);

				if (entry.length > 1) {
					query.put(entry[0], entry[1]);
				} else {
					query.put(entry[0], "");
				}
			}
		}
	}

	public Map<String, String> variables() {
		return variables;
	}

	public Map<String, String> query() {
		return query;
	}

	public String[] path() {
		return path;
	}

	public String body() {
		return body.get();
	}
}
