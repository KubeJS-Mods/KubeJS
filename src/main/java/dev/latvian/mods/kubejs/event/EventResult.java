package dev.latvian.mods.kubejs.event;

import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EventResult {
	public enum Type {
		ERROR,
		PASS,
		INTERRUPT_DEFAULT,
		INTERRUPT_FALSE,
		INTERRUPT_TRUE;

		public final EventResult defaultResult;
		public final EventExit defaultExit;

		Type() {
			this.defaultResult = new EventResult(this, null);
			this.defaultExit = new EventExit(this.defaultResult);
		}

		public EventExit exit(@Nullable Object value) {
			return value == null ? defaultExit : new EventExit(new EventResult(this, value));
		}
	}

	public static final EventResult PASS = Type.PASS.defaultResult;

	private final Type type;
	private final Object value;

	private EventResult(Type type, @Nullable Object value) {
		this.type = type;
		this.value = value;
	}

	public Type type() {
		return type;
	}

	public Object value() {
		return value;
	}

	public boolean override() {
		return type != Type.PASS;
	}

	public boolean pass() {
		return type == Type.PASS;
	}

	public boolean error() {
		return type == Type.ERROR;
	}

	public boolean interruptDefault() {
		return type == Type.INTERRUPT_DEFAULT;
	}

	public boolean interruptFalse() {
		return type == Type.INTERRUPT_FALSE;
	}

	public boolean interruptTrue() {
		return type == Type.INTERRUPT_TRUE;
	}

	public boolean applyCancel(ICancellableEvent event) {
		if (interruptFalse()) {
			event.setCanceled(true);
			return true;
		}

		return false;
	}

	public void applyTristate(Consumer<TriState> consumer) {
		if (interruptFalse()) {
			consumer.accept(TriState.FALSE);
		} else if (interruptTrue()) {
			consumer.accept(TriState.TRUE);
		}
	}
}
