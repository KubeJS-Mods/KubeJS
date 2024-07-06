package dev.latvian.mods.kubejs.error;

public class LegacyError extends KubeRuntimeException {
	public LegacyError(String message) {
		super(message);
	}

	@Override
	public String toString() {
		return getLocalizedMessage();
	}
}
