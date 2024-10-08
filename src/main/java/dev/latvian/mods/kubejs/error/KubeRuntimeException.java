package dev.latvian.mods.kubejs.error;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.MutedError;
import dev.latvian.mods.rhino.RhinoException;

public class KubeRuntimeException extends RuntimeException implements MutedError {
	private SourceLine sourceLine;
	private JsonObject customData;

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

	public KubeRuntimeException customData(String key, JsonElement data) {
		if (customData == null) {
			customData = new JsonObject();
		}

		customData.add(key, data);
		return this;
	}

	public KubeRuntimeException customData(String key, String value) {
		return customData(key, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
	}

	public void apply(ConsoleLine line) {
		Throwable c = this;

		while (c != null) {
			if (c instanceof KubeRuntimeException ex) {
				line.withSourceLine(ex.sourceLine);

				if (ex.customData != null) {
					for (var entry : ex.customData.entrySet()) {
						line.customData(entry.getKey(), entry.getValue(), false);
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