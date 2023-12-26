package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.typings.Info;
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

	@Info("""
		Cancels the event with default exit value. Execution will be stopped **immediately**.
					
		`cancel` denotes a `false` outcome.
		""")
	public Object cancel() throws EventExit {
		return cancel(defaultExitValue());
	}

	@Info("""
		Stops the event with default exit value. Execution will be stopped **immediately**.
					
		`success` denotes a `true` outcome.
		""")
	public Object success() throws EventExit {
		return success(defaultExitValue());
	}

	@Info("""
		Stops the event with default exit value. Execution will be stopped **immediately**.
					
		`exit` denotes a `default` outcome.
		""")
	public Object exit() throws EventExit {
		return exit(defaultExitValue());
	}

	@Info("""
		Cancels the event with the given exit value. Execution will be stopped **immediately**.
					
		`cancel` denotes a `false` outcome.
		""")
	public Object cancel(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_FALSE.exit(mapExitValue(value));
	}

	@Info("""
		Stops the event with the given exit value. Execution will be stopped **immediately**.
					
		`success` denotes a `true` outcome.
		""")
	public Object success(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_TRUE.exit(mapExitValue(value));
	}

	@Info("""
		Stops the event with the given exit value. Execution will be stopped **immediately**.
					
		`exit` denotes a `default` outcome.
		""")
	public Object exit(@Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_DEFAULT.exit(mapExitValue(value));
	}

	protected void afterPosted(EventResult result) {
	}
}