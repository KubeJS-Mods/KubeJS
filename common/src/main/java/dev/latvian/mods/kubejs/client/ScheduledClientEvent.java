package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.client.Minecraft;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.LinkedList;
import java.util.List;

public abstract class ScheduledClientEvent {
	@FunctionalInterface
	public interface Callback {
		void onCallback(ScheduledClientEvent event);
	}

	public static void tickAll(long nowMs, long nowTicks, List<ScheduledClientEvent> kjs$scheduledEvents) {
		if (!kjs$scheduledEvents.isEmpty()) {
			var eventIterator = kjs$scheduledEvents.iterator();
			var list = new LinkedList<ScheduledClientEvent>();

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

	public static class InMs extends ScheduledClientEvent {
		public InMs(Minecraft mc, Duration timer, long e, Callback c) {
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

		public ScheduledClientEvent reschedule(long timer) {
			return client.kjs$schedule(Duration.ofMillis(timer), callback);
		}
	}

	public static class InTicks extends ScheduledClientEvent {
		public InTicks(Minecraft mc, TickDuration t, long e, Callback c) {
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

		public ScheduledClientEvent reschedule(long timer) {
			return client.kjs$schedule(new TickDuration(timer), callback);
		}
	}

	public final Minecraft client;
	public final TemporalAmount duration;
	public final long endTime;
	public final transient Callback callback;

	private ScheduledClientEvent(Minecraft mc, TemporalAmount d, long e, Callback c) {
		client = mc;
		duration = d;
		endTime = e;
		callback = c;
	}

	public void reschedule() {
		reschedule(duration);
	}

	public ScheduledClientEvent reschedule(TemporalAmount timer) {
		return client.kjs$schedule(timer, callback);
	}

	public abstract boolean isUsingTicks();

	public abstract boolean check(long nowMs, long nowTicks);
}