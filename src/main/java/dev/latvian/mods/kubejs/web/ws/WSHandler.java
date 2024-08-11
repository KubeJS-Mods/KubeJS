package dev.latvian.mods.kubejs.web.ws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Supplier;

public interface WSHandler {
	void broadcast(String message);

	void broadcast(byte[] bytes);

	default void broadcast(String event, Supplier<JsonElement> payload) {
		var json = new JsonObject();
		json.addProperty("event", event);
		json.add("payload", payload.get());
		broadcast(json.toString());
	}

	default void broadcast(String event, JsonElement payload) {
		var json = new JsonObject();
		json.addProperty("event", event);
		json.add("payload", payload);
		broadcast(json.toString());
	}
}
