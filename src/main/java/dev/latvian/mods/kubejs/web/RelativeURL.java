package dev.latvian.mods.kubejs.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record RelativeURL(String path, Map<String, String> query) {
	public RelativeURL(String path) {
		this(path, Map.of());
	}

	@Override
	public String toString() {
		var url = new StringBuilder(path);
		boolean first = true;

		for (var entry : query.entrySet()) {
			url.append(first ? '?' : '&').append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append('=').append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
			first = false;
		}

		return url.toString();
	}

	public String fullString() {
		var instance = LocalWebServer.instance();
		return instance == null ? "" : (instance.url() + this);
	}
}
