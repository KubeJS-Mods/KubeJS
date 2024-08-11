package dev.latvian.mods.kubejs.error;

public class MissingRequiredValueException extends KubeRuntimeException {
	public MissingRequiredValueException() {
		super("Missing required value");
	}
}
