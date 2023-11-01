package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ScheduledServerEvent;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
@RemapPrefixForJS("kjs$")
public abstract class MinecraftServerMixin implements MinecraftServerKJS {
	@Shadow
	protected abstract boolean initServer() throws IOException;

	@Shadow
	public abstract void invalidateStatus();

	private final CompoundTag kjs$persistentData = new CompoundTag();
	private final List<ScheduledServerEvent> kjs$scheduledEvents = new LinkedList<>();
	private ServerLevel kjs$overworld;
	private AttachedData<MinecraftServer> kjs$attachedData;

	@Override
	@Accessor("resources")
	public abstract MinecraftServer.ReloadableResources kjs$getReloadableResources();

	@Override
	public CompoundTag kjs$getPersistentData() {
		return kjs$persistentData;
	}

	@Override
	public AttachedData<MinecraftServer> kjs$getData() {
		if (kjs$attachedData == null) {
			kjs$attachedData = new AttachedData<>(kjs$self());
			KubeJSPlugins.forEachPlugin(plugin -> plugin.attachServerData(kjs$attachedData));
		}

		return kjs$attachedData;
	}

	@Override
	public ServerLevel kjs$getOverworld() {
		if (kjs$overworld == null) {
			kjs$overworld = kjs$self().overworld();
		}

		return kjs$overworld;
	}

	@Inject(method = "tickServer", at = @At("RETURN"))
	private void kjs$postTickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
		ScheduledServerEvent.tickAll(System.currentTimeMillis(), kjs$getOverworld().getGameTime(), kjs$scheduledEvents);

		if (ServerEvents.TICK.hasListeners()) {
			ServerEvents.TICK.post(ScriptType.SERVER, new ServerEventJS(kjs$self()));
		}
	}

	@Override
	public ScheduledServerEvent kjs$schedule(TemporalAmount timer, ScheduledServerEvent.Callback event) {
		if (timer instanceof TickDuration duration) {
			var e = new ScheduledServerEvent.InTicks(kjs$self(), duration, kjs$getOverworld().getGameTime() + duration.ticks(), event);
			kjs$scheduledEvents.add(e);
			return e;
		} else if (timer instanceof Duration duration) {
			var e = new ScheduledServerEvent.InMs(kjs$self(), duration, System.currentTimeMillis() + duration.toMillis(), event);
			kjs$scheduledEvents.add(e);
			return e;
		} else {
			throw new IllegalArgumentException("Unsupported TemporalAmount: " + timer);
		}
	}

	@Shadow
	@RemapForJS("isDedicated")
	public abstract boolean isDedicatedServer();

	@Shadow
	@RemapForJS("stop")
	public abstract void stopServer();
}
