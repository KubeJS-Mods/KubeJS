package dev.latvian.mods.kubejs.event;

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

	public EventResult handle(EventJS event, EventHandler.EventExceptionHandler exh) throws EventExit {
		var itr = this;

		do {
			try {
				itr.handler.onEvent(event);
			} catch (EventExit exit) {
				throw exit;
			} catch (Throwable ex) {
				var throwable = ex;

				while (throwable instanceof WrappedException e) {
					throwable = e.getWrappedException();
				}

				if (throwable instanceof EventExit exit) {
					throw exit;
				}

				if (exh == null || (throwable = exh.handle(event, itr, throwable)) != null) {
					throw EventResult.Type.ERROR.exit(throwable);
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
