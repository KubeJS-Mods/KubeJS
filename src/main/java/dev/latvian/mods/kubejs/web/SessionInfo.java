package dev.latvian.mods.kubejs.web;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public record SessionInfo(String source, Set<String> tags) {
	public static final SessionInfo NONE = new SessionInfo("", Set.of());

	public static SessionInfo fromJson(SessionInfo info, JsonObject json) {
		if (json.has("source")) {
			info = new SessionInfo(json.get("source").getAsString(), info.tags());
		}

		if (json.has("tags")) {
			var t = new HashSet<String>();

			for (var e : json.get("tags").getAsJsonArray()) {
				t.add(e.getAsString());
			}

			info = new SessionInfo(info.source(), Set.copyOf(t));
		}

		return info;
	}
}
