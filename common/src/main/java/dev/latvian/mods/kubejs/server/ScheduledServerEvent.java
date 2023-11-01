package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.LinkedList;
import java.util.List;

public abstract class ScheduledServerEvent {
	@FunctionalInterface
	public interface Callback {
		void onCallback(ScheduledServerEvent callback);
	}

	public static void tickAll(long nowMs, long nowTicks, List<ScheduledServerEvent> kjs$scheduledEvents) {
		if (!kjs$scheduledEvents.isEmpty()) {
			var eventIterator = kjs$scheduledEvents.iterator();
			var list = new LinkedList<ScheduledServerEvent>();

			while (eventIterator.hasNext()) {
				var e = eventIterator.next();

				if (e.check(nowMs, nowTicks)) {
					list.add(e);
					eventIterator.remove();
				}
			}

			for (var e : list) {
				try {
					e.callback.onCallback(e);
				} catch (RhinoException ex) {
					ConsoleJS.SERVER.error("Error occurred while handling scheduled event callback: " + ex.getMessage());
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static class InMs extends ScheduledServerEvent {
		public InMs(MinecraftServer mc, Duration timer, long e, Callback c) {
			super(mc, timer, e, c);
		}

		@Override
		public boolean isUsingTicks() {
			return false;
		}

		@Override
		public boolean check(long nowMs, long nowTicks) {
			return nowMs >= endTime;
		}

		public ScheduledServerEvent reschedule(long timer) {
			return server.kjs$schedule(Duration.ofMillis(timer), callback);
		}
	}

	public static class InTicks extends ScheduledServerEvent {
		public InTicks(MinecraftServer mc, TickDuration t, long e, Callback c) {
			super(mc, t, e, c);
		}

		@Override
		public boolean isUsingTicks() {
			return true;
		}

		@Override
		public boolean check(long nowMs, long nowTicks) {
			return nowTicks >= endTime;
		}

		public ScheduledServerEvent reschedule(long timer) {
			return server.kjs$schedule(new TickDuration(timer), callback);
		}
	}

	public final MinecraftServer server;
	public final TemporalAmount duration;
	public final long endTime;
	public final transient Callback callback;

	private ScheduledServerEvent(MinecraftServer mc, TemporalAmount d, long e, Callback c) {
		server = mc;
		duration = d;
		endTime = e;
		callback = c;
	}

	public void reschedule() {
		reschedule(duration);
	}

	public ScheduledServerEvent reschedule(TemporalAmount timer) {
		return server.kjs$schedule(timer, callback);
	}

	public abstract boolean isUsingTicks();

	public abstract boolean check(long nowMs, long nowTicks);
}