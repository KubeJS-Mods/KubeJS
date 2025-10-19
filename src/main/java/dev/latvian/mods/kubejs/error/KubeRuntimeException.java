package dev.latvian.mods.kubejs.error;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.MutedError;
import dev.latvian.mods.rhino.RhinoException;

import java.util.LinkedHashMap;
import java.util.Map;

public class KubeRuntimeException extends RuntimeException implements MutedError {
	private SourceLine sourceLine;
	private Map<String, Object> customData;

	public KubeRuntimeException(String m) {
		super(m);
		this.sourceLine = SourceLine.UNKNOWN;
	}

	public KubeRuntimeException(String m, Throwable cause) {
		super(m, cause);
		this.sourceLine = SourceLine.UNKNOWN;
	}

	public KubeRuntimeException(Throwable cause) {
		super(cause);
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

	public KubeRuntimeException customData(String key, Object data) {
		if (customData == null) {
			customData = new LinkedHashMap<>();
		}

		customData.put(key, data);
		return this;
	}

	public void apply(ConsoleLine line) {
		Throwable c = this;

		while (c != null) {
			if (c instanceof KubeRuntimeException ex) {
				line.withSourceLine(ex.sourceLine);

				if (ex.customData != null) {
					for (var entry : ex.customData.entrySet()) {
						line.customData(entry.getKey(), entry.getValue() == null ? JsonNull.INSTANCE : new JsonPrimitive(entry.getValue().toString()), false);
					}
				}
			}

			if (c instanceof RhinoException ex) {
				if (ex.lineSource() != null) {
					line.withSourceLine(ex.lineSource(), ex.lineNumber());
				}
			}

			c = c.getCause();
		}
	}
}