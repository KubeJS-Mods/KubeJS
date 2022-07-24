package dev.latvian.mods.kubejs.server;

import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class ScheduledEvent {
	private final MinecraftServer server;
	private final boolean usingTicks;
	private final long timer;
	private final long endTime;
	private final IScheduledEventCallback callback;

	public ScheduledEvent(MinecraftServer s, boolean ut, long t, long e, IScheduledEventCallback c) {
		usingTicks = ut;
		server = s;
		timer = t;
		endTime = e;
		callback = c;
	}

	public boolean isUsingTicks() {
		return usingTicks;
	}

	public MinecraftServer getServer() {
		return server;
	}

	public long getTimer() {
		return timer;
	}

	public long getEndTime() {
		return endTime;
	}

	public void reschedule() {
		reschedule(timer);
	}

	public long getTimerDuration() {
		return endTime - timer;
	}

	public ScheduledEvent reschedule(long timer) {
		if (isUsingTicks()) {
			return server.kjs$scheduleInTicks(timer, callback);
		} else {
			return server.kjs$schedule(timer, callback);
		}
	}

	void call() {
		callback.onCallback(this);
	}
}