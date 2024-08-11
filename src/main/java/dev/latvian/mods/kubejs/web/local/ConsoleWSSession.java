package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.web.ws.WSSession;

public class ConsoleWSSession extends WSSession {
	public final ConsoleJS console;

	public ConsoleWSSession(ConsoleJS console) {
		this.console = console;
	}

	@Override
	public void onEvent(String event, JsonElement payload) {
		switch (event) {
			case "info" -> console.info(payload.getAsString());
			case "warn" -> console.warn(payload.getAsString());
			case "error" -> console.error(payload.getAsString());
		}
	}
}
