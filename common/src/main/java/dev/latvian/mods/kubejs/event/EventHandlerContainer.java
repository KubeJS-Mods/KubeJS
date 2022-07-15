package dev.latvian.mods.kubejs.event;

import org.jetbrains.annotations.Nullable;

class EventHandlerContainer {
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

	private final IEventHandler handler;
	private EventHandlerContainer child;

	public EventHandlerContainer(IEventHandler handler) {
		this.handler = handler;
	}

	public boolean handle(EventJS event, boolean cancelable) {
		var itr = this;

		while (itr != null) {
			itr.handler.onEvent(event);

			if (cancelable && event.isCanceled()) {
				return true;
			}

			itr = itr.child;
		}

		return false;
	}

	public void add(IEventHandler handler) {
		var itr = this;

		while (itr.child != null) {
			itr = itr.child;
		}

		itr.child = new EventHandlerContainer(handler);
	}
}
