package dev.latvian.kubejs.event;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.script.ScriptType;

/**
 * @author LatvianModder
 */
public class EventJS
{
	private boolean cancelled = false;

	public boolean canCancel()
	{
		return false;
	}

	public final void cancel()
	{
		cancelled = true;
	}

	@Ignore
	public final boolean isCancelled()
	{
		return cancelled;
	}

	@Ignore
	public final boolean post(ScriptType t, String id)
	{
		if (t != ScriptType.STARTUP && post(ScriptType.STARTUP, id) && canCancel())
		{
			return true;
		}

		EventsJS e = t.manager.get().events;
		return e.postToHandlers(id, e.handlers(id), this);
	}

	@Ignore
	public final boolean post(ScriptType t, String id, String sub)
	{
		if (t != ScriptType.STARTUP && post(ScriptType.STARTUP, id, sub) && canCancel())
		{
			return true;
		}

		EventsJS e = t.manager.get().events;
		String id1 = id + '.' + sub;
		return e.postToHandlers(id1, e.handlers(id1), this) || e.postToHandlers(id, e.handlers(id), this);
	}
}