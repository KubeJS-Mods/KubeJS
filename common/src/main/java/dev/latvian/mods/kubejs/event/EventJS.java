package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;

/**
 * @author LatvianModder
 */
public class EventJS {
	private boolean canceled = false;

	public final void cancel() {
		canceled = true;
	}

	public final boolean isCanceled() {
		return canceled;
	}

	protected void afterPosted(boolean isCanceled) {
	}

	public final boolean post(ScriptType t, String id) {
		t.console.error("post() for event '" + id + "' is no longer supported!");
		afterPosted(false);
		return false;
	}
}