package dev.latvian.mods.kubejs.event;

import org.jetbrains.annotations.Nullable;

public class EventJS {
	@Nullable
	protected Object defaultExitValue() {
		return null;
	}

	@Nullable
	protected Object mapExitValue(@Nullable Object value) {
		return value;
	}

	public Object cancel() throws EventExit {
		return cancel(defaultExitValue());
	}

	public Object success() throws EventExit {
		return success(defaultExitValue());
	}

	public Object exit() throws EventExit {
		return exit(defaultExitValue());
	}

	public Object cancel(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_FALSE.exit(mapExitValue(value));
	}

	public Object success(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_TRUE.exit(mapExitValue(value));
	}

	public Object exit(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_DEFAULT.exit(mapExitValue(value));
	}

	protected void afterPosted(EventResult result) {
	}
}