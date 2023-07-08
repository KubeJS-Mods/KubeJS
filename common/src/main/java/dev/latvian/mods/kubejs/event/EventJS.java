package dev.latvian.mods.kubejs.event;

import org.jetbrains.annotations.Nullable;

public class EventJS {
	public Object cancel() {
		throw EventResult.Type.INTERRUPT_FALSE.defaultExit;
	}

	public Object success() {
		throw EventResult.Type.INTERRUPT_TRUE.defaultExit;
	}

	public Object exit() {
		throw EventResult.Type.INTERRUPT_DEFAULT.defaultExit;
	}

	public Object cancel(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_FALSE.exit(value);
	}

	public Object success(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_TRUE.exit(value);
	}

	public Object exit(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_DEFAULT.exit(value);
	}

	protected void afterPosted(EventResult result) {
	}
}