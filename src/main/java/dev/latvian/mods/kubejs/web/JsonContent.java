package dev.latvian.mods.kubejs.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.apps.tinyserver.content.ResponseContent;
import dev.latvian.mods.kubejs.util.Lazy;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record JsonContent(Lazy<byte[]> json) implements ResponseContent {
	public static JsonContent any(Supplier<JsonElement> json) {
		return new JsonContent(Lazy.of(() -> json.get().toString().getBytes(StandardCharsets.UTF_8)));
	}

	public static JsonContent object(Consumer<JsonObject> json) {
		return new JsonContent(Lazy.of(() -> {
			var t = new JsonObject();
			json.accept(t);
			return t.toString().getBytes(StandardCharsets.UTF_8);
		}));
	}

	public static JsonContent array(Consumer<JsonArray> json) {
		return new JsonContent(Lazy.of(() -> {
			var t = new JsonArray();
			json.accept(t);
			return t.toString().getBytes(StandardCharsets.UTF_8);
		}));
	}

	@Override
	public long length() {
		return json.get().length;
	}

	@Override
	public String type() {
		return "application/json; charset=utf-8";
	}

	@Override
	public void write(OutputStream out) throws Exception {
		out.write(json.get());
	}
}
