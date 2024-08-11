package dev.latvian.mods.kubejs.error;

import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.MutedError;

public class KubeRuntimeException extends RuntimeException implements MutedError {
	public SourceLine sourceLine;

	public KubeRuntimeException(String m) {
		super(m);
		this.sourceLine = SourceLine.UNKNOWN;
	}

	public KubeRuntimeException(String m, Throwable cause) {
		super(m, cause);
		this.sourceLine = SourceLine.UNKNOWN;
	}

	@Override
	public String toString() {
		String message = getLocalizedMessage();
		return message != null && !message.isEmpty() ? message : getClass().getName();
	}

	public KubeRuntimeException source(SourceLine sourceLine) {
		if (this.sourceLine.isUnknown()) {
			this.sourceLine = sourceLine;
		}

		return this;
	}
}