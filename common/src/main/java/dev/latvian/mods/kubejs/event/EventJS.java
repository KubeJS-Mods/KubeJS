package dev.latvian.mods.kubejs.event;

import org.jetbrains.annotations.Nullable;

public class EventJS {
	public void cancel() {
		throw EventResult.Type.INTERRUPT_FALSE.defaultExit;
	}

	public void success() {
		throw EventResult.Type.INTERRUPT_TRUE.defaultExit;
	}

	public void exit() {
		throw EventResult.Type.INTERRUPT_DEFAULT.defaultExit;
	}

	public void cancel(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_FALSE.exit(value);
	}

	public void success(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_TRUE.exit(value);
	}

	public void exit(@Nullable Object value) {
		throw EventResult.Type.INTERRUPT_DEFAULT.exit(value);
	}

	protected void afterPosted(EventResult result) {
	}
}