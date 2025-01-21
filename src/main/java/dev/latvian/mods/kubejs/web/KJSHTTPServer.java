package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import net.minecraft.util.thread.BlockableEventLoop;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class KJSHTTPServer extends HTTPServer<KJSHTTPRequest> {
	record RequestFactory(BlockableEventLoop<?> eventLoop) implements Supplier<KJSHTTPRequest> {
		@Override
		public KJSHTTPRequest get() {
			return new KJSHTTPRequest(eventLoop);
		}
	}

	public final transient String auth;
	public final transient String encodedAuth;

	KJSHTTPServer(RequestFactory requestFactory, String auth) {
		super(requestFactory);
		this.auth = auth;
		this.encodedAuth = auth.isEmpty() ? "" : URLEncoder.encode(auth, StandardCharsets.UTF_8);
	}
}
