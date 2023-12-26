package dev.latvian.mods.kubejs.event;

import dev.architectury.event.CompoundEventResult;
import dev.latvian.mods.kubejs.util.UtilsJS;
import org.jetbrains.annotations.Nullable;

public class EventResult {
	public enum Type {
		ERROR(dev.architectury.event.EventResult.pass()),
		PASS(dev.architectury.event.EventResult.pass()),
		INTERRUPT_DEFAULT(dev.architectury.event.EventResult.interruptDefault()),
		INTERRUPT_FALSE(dev.architectury.event.EventResult.interruptFalse()),
		INTERRUPT_TRUE(dev.architectury.event.EventResult.interruptTrue());

		public final EventResult defaultResult;
		public final dev.architectury.event.EventResult defaultArchResult;
		public final EventExit defaultExit;

		Type(dev.architectury.event.EventResult defaultArchResult) {
			this.defaultResult = new EventResult(this, null);
			this.defaultArchResult = defaultArchResult;
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

	public dev.architectury.event.EventResult arch() {
		return type.defaultArchResult;
	}

	public <T> CompoundEventResult<T> archCompound() {
		return switch (type) {
			case INTERRUPT_DEFAULT -> CompoundEventResult.interruptDefault(UtilsJS.cast(value));
			case INTERRUPT_FALSE -> CompoundEventResult.interruptFalse(UtilsJS.cast(value));
			case INTERRUPT_TRUE -> CompoundEventResult.interruptTrue(UtilsJS.cast(value));
			default -> CompoundEventResult.pass();
		};
	}
}
