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
		if (t != ScriptType.STARTUP) {
			post(ScriptType.STARTUP, id);
		}

		var e = t.manager.get().events;
		e.postToHandlers(id, e.handlers(id), this);
		afterPosted(false);
		return false;
	}
}