package dev.latvian.mods.kubejs.event;

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
}