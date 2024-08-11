package dev.latvian.mods.kubejs.web.http;

public interface HTTPHandler<CTX extends HTTPContext> {
	HTTPResponse handle(CTX ctx) throws Exception;
}
