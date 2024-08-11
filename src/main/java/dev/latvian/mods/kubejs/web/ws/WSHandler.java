package dev.latvian.mods.kubejs.web.ws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface WSHandler {
	WSHandler EMPTY = new WSHandler() {
		@Override
		public void broadcast(String message) {
		}

		@Override
		public void broadcast(byte[] bytes) {
		}

		@Override
		public void broadcast(String event, Supplier<JsonElement> payload) {
		}

		@Override
		public void broadcast(String event, String payload) {
		}

		@Override
		public void broadcast(String event, @Nullable JsonElement payload) {
		}
	};

	void broadcast(String message);

	void broadcast(byte[] bytes);

	default void broadcast(String event, Supplier<JsonElement> payload) {
		broadcast(event, payload.get());
	}

	default void broadcast(String event, String payload) {
		broadcast(event, new JsonPrimitive(payload));
	}

	default void broadcast(String event, @Nullable JsonElement payload) {
		var json = new JsonObject();
		json.addProperty("event", event);

		if (payload != null && !payload.isJsonNull()) {
			json.add("payload", payload);
		}

		broadcast(json.toString());
	}
}
