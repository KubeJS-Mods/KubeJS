package dev.latvian.mods.kubejs.web.http;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.Nullable;

public interface HTTPResponse {
	HTTPResponse OK = SimpleHTTPResponse.text(200, "OK");
	HTTPResponse NO_CONTENT = new SimpleHTTPResponse(204, null, "");
	HTTPResponse WIP = SimpleHTTPResponse.text(404, "WIP");
	HTTPResponse NOT_FOUND = SimpleHTTPResponse.text(404, "Not Found");

	int status();

	default String contentType() {
		return "";
	}

	@Nullable
	default byte[] body() {
		return null;
	}

	default void respond(HttpExchange exchange) throws Exception {
		var bytes = body();
		var contentType = contentType();

		if (!contentType.isEmpty()) {
			exchange.getResponseHeaders().set("Content-Type", contentType);
		}

		exchange.sendResponseHeaders(status(), bytes == null ? -1L : bytes.length);

		if (bytes != null) {
			try (var os = exchange.getResponseBody()) {
				os.write(bytes);
			}
		}
	}
}
