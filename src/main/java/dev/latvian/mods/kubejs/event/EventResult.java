package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.rhino.Context;
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

		private final EventResult defaultResult;
		private final EventExit defaultExit;

		Type() {
			this.defaultResult = new EventResult(null, this, null);
			this.defaultExit = new EventExit(this.defaultResult);
		}

		public EventExit exit(@Nullable Context cx, @Nullable Object value) {
			return value == null ? defaultExit : new EventExit(new EventResult(cx, this, value));
		}
	}

	public static final EventResult PASS = Type.PASS.defaultResult;

	private final Context cx;
	private final Type type;
	private final Object value;

	private EventResult(@Nullable Context cx, Type type, @Nullable Object value) {
		this.cx = cx;
		this.type = type;
		this.value = value;
	}

	@Nullable
	public Context cx() {
		return cx;
	}

	public Type type() {
		return type;
	}

	@Nullable
	public Object value() {
		return value;
	}

	public boolean override() {
		return type != Type.PASS;
	}

	public boolean pass() {
		return type == Type.PASS;
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
