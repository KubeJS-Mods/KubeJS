package dev.latvian.mods.kubejs.web.http;

import com.sun.net.httpserver.HttpExchange;

public interface HTTPResponse {
	HTTPResponse OK = SimpleHTTPResponse.text(200, "OK");
	HTTPResponse NO_CONTENT = new SimpleHTTPResponse(204, null, "");
	HTTPResponse WIP = SimpleHTTPResponse.text(404, "WIP");
	HTTPResponse NOT_FOUND = SimpleHTTPResponse.text(404, "Not Found");

	void respond(HttpExchange exchange) throws Exception;

	default HTTPResponse withHeader(String header, String value) {
		return new HTTPResponseWithHeader(this, header, value);
	}
}
