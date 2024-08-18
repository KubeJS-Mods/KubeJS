package dev.latvian.mods.kubejs.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.mods.kubejs.util.JsonUtils;

public class KJSWSSession extends WSSession<KJSHTTPRequest> {
	@Override
	public void onTextMessage(String message) {
		if (message.startsWith("{") && message.endsWith("}")) {
			var json = JsonUtils.fromString(message).getAsJsonObject();

			if (json.has("type")) {
				onEvent(json.get("type").getAsString(), json.has("payload") ? json.get("payload") : JsonNull.INSTANCE);
			}
		}
	}

	public void onEvent(String type, JsonElement payload) {
	}
}
