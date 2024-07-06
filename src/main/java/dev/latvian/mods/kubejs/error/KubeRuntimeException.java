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
		sb.append(getMessage());

		// append cause as well since RecipeExceptions can swallow underlying problems
		if (getCause() != null) {
			sb.append("\ncause: ");
			sb.append(getCause());
		}

		return sb.toString();
	}

	public KubeRuntimeException source(SourceLine sourceLine) {
		this.sourceLine = sourceLine;
		return this;
	}
}