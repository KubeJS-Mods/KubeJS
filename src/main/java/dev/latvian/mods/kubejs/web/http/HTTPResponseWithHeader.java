package dev.latvian.mods.kubejs.web.http;

import com.sun.net.httpserver.HttpExchange;

public record HTTPResponseWithHeader(HTTPResponse original, String header, String value) implements HTTPResponse {
	@Override
	public void respond(HttpExchange exchange) throws Exception {
		exchange.getResponseHeaders().set(header, value);
		original.respond(exchange);
	}
}
