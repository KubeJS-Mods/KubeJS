package dev.latvian.mods.kubejs.util;

import org.slf4j.Logger;

import java.util.function.BiConsumer;

public enum LogType {
	INIT("INIT", Logger::info),
	DEBUG("DEBUG", Logger::debug),
	INFO("INFO", Logger::info),
	WARN("WARN", Logger::warn),
	ERROR("ERROR", Logger::error);

	public final String name;
	public final BiConsumer<Logger, String> callback;

	LogType(String name, BiConsumer<Logger, String> callback) {
		this.name = name;
		this.callback = callback;
	}
}
