package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import net.minecraft.util.thread.BlockableEventLoop;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class LocalWebServerRegistryHolder {
	private final BlockableEventLoop<?> eventLoop;
	final String auth;
	final String encodedAuth;
	final HTTPServer<KJSHTTPRequest> server;

	public LocalWebServerRegistryHolder(BlockableEventLoop<?> eventLoop, String auth) {
		this.eventLoop = eventLoop;
		this.auth = auth;
		this.encodedAuth = auth.isEmpty() ? "" : URLEncoder.encode(auth, StandardCharsets.UTF_8);
		this.server = new HTTPServer<>(this::createRequest);
	}

	private KJSHTTPRequest createRequest() {
		return new KJSHTTPRequest(eventLoop);
	}
}
