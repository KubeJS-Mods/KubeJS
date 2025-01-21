package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.HTTPHandler;
import dev.latvian.apps.tinyserver.http.HTTPMethod;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.error.client.ForbiddenError;
import dev.latvian.apps.tinyserver.http.response.error.client.UnauthorizedError;
import dev.latvian.apps.tinyserver.ws.WSEndpointHandler;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.apps.tinyserver.ws.WSSessionFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalWebServerRegistry implements ServerRegistry<KJSHTTPRequest> {
	private record AuthHandler(HTTPHandler<KJSHTTPRequest> handler, String match) implements HTTPHandler<KJSHTTPRequest> {
		@Override
		public HTTPResponse handle(KJSHTTPRequest req) throws Exception {
			var a = req.header("Authorization").asString();

			if (a.isEmpty()) {
				throw new UnauthorizedError("Missing Authorization header");
			} else if (!a.equals(match)) {
				throw new ForbiddenError("Authorization header does not match configured auth token");
			}

			return handler.handle(req);
		}
	}

	private final LocalWebServerRegistryHolder holder;
	private final Set<LocalWebServer.Endpoint> endpoints;
	private final boolean requireAuth;

	LocalWebServerRegistry(LocalWebServerRegistryHolder holder, Set<LocalWebServer.Endpoint> endpoints, boolean requireAuth) {
		this.holder = holder;
		this.endpoints = endpoints;
		this.requireAuth = requireAuth;
	}

	private HTTPHandler<KJSHTTPRequest> wrap(HTTPHandler<KJSHTTPRequest> handler) {
		return requireAuth ? new AuthHandler(handler, "Bearer " + holder.auth) : handler;
	}

	@Override
	public void http(HTTPMethod method, String path, HTTPHandler<KJSHTTPRequest> handler) {
		endpoints.add(new LocalWebServer.Endpoint(method.name(), path, requireAuth));
		holder.server.http(method, path, wrap(handler));
	}

	@Override
	public <WSS extends WSSession<KJSHTTPRequest>> WSHandler<KJSHTTPRequest, WSS> ws(String path, WSSessionFactory<KJSHTTPRequest, WSS> factory) {
		endpoints.add(new LocalWebServer.Endpoint("WS", path, requireAuth));
		var handler = new WSEndpointHandler<>(factory, new ConcurrentHashMap<>());
		holder.server.http(HTTPMethod.GET, path, wrap(handler));
		return handler;
	}
}
