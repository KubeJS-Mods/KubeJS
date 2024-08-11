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
		var sb = new StringBuilder();

		var message = getLocalizedMessage();

		if (message != null) {
			sb.append(message);
		} else {
			sb.append(getClass().getName());
		}

		var c = getCause();

		while (c != null) {
			sb.append(" - ").append(c);
			c = c.getCause();
		}

		return sb.toString();
	}

	public KubeRuntimeException source(SourceLine sourceLine) {
		if (this.sourceLine.isUnknown()) {
			this.sourceLine = sourceLine;
		}

		return this;
	}
}