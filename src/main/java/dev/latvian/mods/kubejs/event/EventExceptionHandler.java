package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.rhino.WrappedException;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface EventExceptionHandler {
	/// Handles an exception thrown by an event handler.
	///
	/// @param event     The event being posted
	/// @param container The event handler container that threw the exception
	/// @param ex        The exception that was thrown
	/// @return `null` if the exception could be recovered from, otherwise the exception that should be rethrown
	/// @implNote The thrown exception will never be an instance of [EventExit] or [WrappedException],
	/// as those are already handled by the container itself.
	@Nullable Throwable handle(KubeEvent event, EventHandlerContainer container, Throwable ex);
}