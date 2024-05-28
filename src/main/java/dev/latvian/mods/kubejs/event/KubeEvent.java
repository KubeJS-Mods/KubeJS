package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

public interface KubeEvent {
	@Nullable
	@HideFromJS
	default Object defaultExitValue(Context cx) {
		return null;
	}

	@Nullable
	@HideFromJS
	default Object mapExitValue(Context cx, @Nullable Object value) {
		return value;
	}

	@Info("""
		Cancels the event with default exit value. Execution will be stopped **immediately**.
					
		`cancel` denotes a `false` outcome.
		""")
	default Object cancel(Context cx) throws EventExit {
		return cancel(cx, defaultExitValue(cx));
	}

	@Info("""
		Stops the event with default exit value. Execution will be stopped **immediately**.
					
		`success` denotes a `true` outcome.
		""")
	default Object success(Context cx) throws EventExit {
		return success(cx, defaultExitValue(cx));
	}

	@Info("""
		Stops the event with default exit value. Execution will be stopped **immediately**.
					
		`exit` denotes a `default` outcome.
		""")
	default Object exit(Context cx) throws EventExit {
		return exit(cx, defaultExitValue(cx));
	}

	@Info("""
		Cancels the event with the given exit value. Execution will be stopped **immediately**.
					
		`cancel` denotes a `false` outcome.
		""")
	default Object cancel(Context cx, @Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_FALSE.exit(mapExitValue(cx, value));
	}

	@Info("""
		Stops the event with the given exit value. Execution will be stopped **immediately**.
					
		`success` denotes a `true` outcome.
		""")
	default Object success(Context cx, @Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_TRUE.exit(mapExitValue(cx, value));
	}

	@Info("""
		Stops the event with the given exit value. Execution will be stopped **immediately**.
					
		`exit` denotes a `default` outcome.
		""")
	default Object exit(Context cx, @Nullable Object value) throws EventExit {
		throw EventResult.Type.INTERRUPT_DEFAULT.exit(mapExitValue(cx, value));
	}

	@HideFromJS
	default void afterPosted(EventResult result) {
	}
}