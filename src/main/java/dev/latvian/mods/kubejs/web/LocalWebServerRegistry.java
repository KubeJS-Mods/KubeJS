package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.HTTPHandler;
import dev.latvian.apps.tinyserver.http.HTTPMethod;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.apps.tinyserver.ws.WSSessionFactory;
import net.minecraft.util.thread.BlockableEventLoop;

import java.util.HashSet;

public class LocalWebServerRegistry implements ServerRegistry<KJSHTTPRequest> {
	private final BlockableEventLoop<?> eventLoop;
	final HTTPServer<KJSHTTPRequest> server;
	final HashSet<LocalWebServer.Endpoint> endpoints;

	public LocalWebServerRegistry(BlockableEventLoop<?> eventLoop) {
		this.eventLoop = eventLoop;
		this.server = new HTTPServer<>(this::createRequest);
		this.endpoints = new HashSet<>();
	}

	private KJSHTTPRequest createRequest() {
		KJSHTTPRequest request = new KJSHTTPRequest();
		request.eventLoop = eventLoop;
		return request;
	}

	@Override
	public void http(HTTPMethod method, String path, HTTPHandler<KJSHTTPRequest> handler) {
		server.http(method, path, handler);
		endpoints.add(new LocalWebServer.Endpoint(method.name(), path));
	}

	@Override
	public <WSS extends WSSession<KJSHTTPRequest>> WSHandler<KJSHTTPRequest, WSS> ws(String path, WSSessionFactory<KJSHTTPRequest, WSS> factory) {
		endpoints.add(new LocalWebServer.Endpoint("WS", path));
		return server.ws(path, factory);
	}
}
