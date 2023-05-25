package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

/**
 * @author LatvianModder
 */
public abstract class ScheduledEvent {
	public static class InMs extends ScheduledEvent {
		public InMs(MinecraftServer s, Duration timer, long e, IScheduledEventCallback c) {
			super(s, timer, e, c);
		}

		@Override
		public boolean isUsingTicks() {
			return false;
		}

		@Override
		public boolean check(long nowMs, long nowTicks) {
			return nowMs >= endTime;
		}

		public ScheduledEvent reschedule(long timer) {
			return server.kjs$schedule(Duration.ofMillis(timer), callback);
		}
	}

	public static class InTicks extends ScheduledEvent {
		public InTicks(MinecraftServer s, TickDuration t, long e, IScheduledEventCallback c) {
			super(s, t, e, c);
		}

		@Override
		public boolean isUsingTicks() {
			return true;
		}

		@Override
		public boolean check(long nowMs, long nowTicks) {
			return nowTicks >= endTime;
		}

		public ScheduledEvent reschedule(long timer) {
			return server.kjs$schedule(new TickDuration(timer), callback);
		}
	}

	public final MinecraftServer server;
	public final TemporalAmount duration;
	public final long endTime;
	public final transient IScheduledEventCallback callback;

	private ScheduledEvent(MinecraftServer s, TemporalAmount d, long e, IScheduledEventCallback c) {
		server = s;
		duration = d;
		endTime = e;
		callback = c;
	}

	public void reschedule() {
		reschedule(duration);
	}

	public ScheduledEvent reschedule(TemporalAmount timer) {
		return server.kjs$schedule(timer, callback);
	}

	public abstract boolean isUsingTicks();

	public abstract boolean check(long nowMs, long nowTicks);
}