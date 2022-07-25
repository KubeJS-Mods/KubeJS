package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.server.IScheduledEventCallback;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.mods.kubejs.server.ScheduledEvent;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author LatvianModder
 */
@Mixin(MinecraftServer.class)
@RemapPrefixForJS("kjs$")
public abstract class MinecraftServerMixin implements MinecraftServerKJS {
	private final CompoundTag kjs$persistentData = new CompoundTag();
	private final List<ScheduledEvent> kjs$scheduledEvents = new LinkedList<>();
	private final List<ScheduledEvent> kjs$scheduledTickEvents = new LinkedList<>();
	private ServerLevel kjs$overworld;
	private AttachedData<MinecraftServer> kjs$attachedData;

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
		KubeJSServerEventHandler.tickScheduledEvents(System.currentTimeMillis(), kjs$scheduledEvents);
		KubeJSServerEventHandler.tickScheduledEvents(kjs$getOverworld().getGameTime(), kjs$scheduledTickEvents);
		ServerEvents.TICK.post(new ServerEventJS(kjs$self()));
	}

	@Override
	public ScheduledEvent kjs$schedule(long timer, IScheduledEventCallback event) {
		var e = new ScheduledEvent(kjs$self(), false, timer, System.currentTimeMillis() + timer, event);
		kjs$scheduledEvents.add(e);
		return e;
	}

	@Override
	public ScheduledEvent kjs$scheduleInTicks(long ticks, IScheduledEventCallback event) {
		var e = new ScheduledEvent(kjs$self(), true, ticks, kjs$getOverworld().getGameTime() + ticks, event);
		kjs$scheduledTickEvents.add(e);
		return e;
	}

	@Shadow(remap = false)
	@RemapForJS("isDedicated")
	public abstract boolean isDedicatedServer();

	@Shadow
	@RemapForJS("stop")
	public abstract void close();
}
