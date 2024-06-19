package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.WrappedException;
import org.jetbrains.annotations.Nullable;

public class EventHandlerContainer {
	public static boolean isEmpty(@Nullable EventHandlerContainer[] array) {
		if (array == null) {
			return true;
		}

		for (var c : array) {
			if (c != null) {
				return false;
			}
		}

		return true;
	}

	public final Object extraId;
	public final IEventHandler handler;
	public final String source;
	public final int line;
	EventHandlerContainer child;

	public EventHandlerContainer(Object extraId, IEventHandler handler, String source, int line) {
		this.extraId = extraId;
		this.handler = handler;
		this.source = source;
		this.line = line;
	}

	public EventResult handle(ConsoleJS console, EventHandler handler, KubeEvent event) throws EventExit {
		var itr = this;

		do {
			try {
				itr.handler.onEvent(event);
			} catch (EventExit exit) {
				if (handler.getResult() == null) {
					console.error("Error in '" + this + "': Event returned result when it's not cancellable");
				} else {
					throw exit;
				}
			} catch (Throwable ex) {
				var throwable = ex;

				while (throwable instanceof WrappedException e) {
					throwable = e.getWrappedException();
				}

				if (throwable instanceof EventExit exit) {
					if (handler.getResult() == null) {
						console.error("Error in '" + this + "': Event returned result when it's not cancellable");
					} else {
						throw exit;
					}
				}

				if (handler.exceptionHandler == null || (throwable = handler.exceptionHandler.handle(event, itr, throwable)) != null) {
					console.error("Error in '" + handler + "'", throwable);

					if (DevProperties.get().logEventErrorStackTrace) {
						throwable.printStackTrace();
					}
				}
			}

			itr = itr.child;
		}
		while (itr != null);

		return EventResult.PASS;
	}

	public void add(Object extraId, IEventHandler handler, String source, int line) {
		var itr = this;

		while (itr.child != null) {
			itr = itr.child;
		}

		itr.child = new EventHandlerContainer(extraId, handler, source, line);
	}

	@Override
	public String toString() {
		return "Event Handler (" + source + ":" + line + ")";
	}
}
