package dev.latvian.mods.kubejs.web.local;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.web.KJSWSSession;

public class ConsoleWSSession extends KJSWSSession {
	public final ConsoleJS console;

	public ConsoleWSSession(ConsoleJS console) {
		this.console = console;
	}

	@Override
	public void onEvent(String type, JsonElement payload) {
		switch (type) {
			case "info" -> console.info(payload.getAsString());
			case "warn" -> console.warn(payload.getAsString());
			case "error" -> console.error(payload.getAsString());
		}
	}
}
