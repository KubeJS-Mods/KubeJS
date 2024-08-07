package dev.latvian.mods.kubejs.web.http;

public interface HTTPHandler {
	HTTPResponse handle(HTTPContext ctx) throws Exception;
}
