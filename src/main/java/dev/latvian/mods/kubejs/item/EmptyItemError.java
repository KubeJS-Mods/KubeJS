package dev.latvian.mods.kubejs.item;

public class EmptyItemError extends IllegalArgumentException {
	public final Object from;

	public EmptyItemError(String message, Object from) {
		super(message);
		this.from = from;
	}
}
