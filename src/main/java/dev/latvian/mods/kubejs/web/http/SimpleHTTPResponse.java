package dev.latvian.mods.kubejs.web.http;

import com.google.gson.JsonElement;
import com.sun.net.httpserver.HttpExchange;
import dev.latvian.mods.kubejs.util.JsonUtils;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public record SimpleHTTPResponse(int status, @Nullable byte[] body, String contentType) implements HTTPResponse {
	public static SimpleHTTPResponse text(int status, String body) {
		return new SimpleHTTPResponse(status, body.getBytes(StandardCharsets.UTF_8), "text/plain; charset=utf-8");
	}

	public static SimpleHTTPResponse text(int status, Iterable<String> lines) {
		return text(status, String.join("\n", lines));
	}

	public static SimpleHTTPResponse json(int status, JsonElement json) {
		return new SimpleHTTPResponse(status, JsonUtils.GSON.toJson(json).getBytes(StandardCharsets.UTF_8), "application/json; charset=utf-8");
	}

	public static SimpleHTTPResponse lazyJson(Supplier<JsonElement> json) {
		return json(200, json.get());
	}

	@Override
	public void respond(HttpExchange exchange) throws Exception {
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
