package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.server.IScheduledEventCallback;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import dev.latvian.mods.kubejs.server.ScheduledEvent;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
	private final Map<ResourceLocation, ServerLevelJS> kjs$levelMap = new HashMap<>();
	private final Map<UUID, ServerPlayerDataJS> kjs$playerMap = new HashMap<>();
	private final Map<UUID, FakeServerPlayerDataJS> kjs$fakePlayerMap = new HashMap<>();
	private final List<ServerLevelJS> kjs$allLevels = new ArrayList<>();
	private ServerLevelJS kjs$overworld;
	private AttachedData kjs$data;

	@Override
	@RemapForJS("getMinecraftServer")
	public MinecraftServer kjs$self() {
		return (MinecraftServer) (Object) this;
	}

	@Override
	public CompoundTag kjs$getPersistentData() {
		return kjs$persistentData;
	}

	@Override
	public AttachedData kjs$getData() {
		if (kjs$data == null) {
			kjs$data = new AttachedData(this);
		}

		return kjs$data;
	}

	@Override
	@HideFromJS
	public Map<ResourceLocation, ServerLevelJS> kjs$getLevelMap() {
		return kjs$levelMap;
	}

	@Override
	@HideFromJS
	public Map<UUID, ServerPlayerDataJS> kjs$getPlayerMap() {
		return kjs$playerMap;
	}

	@Override
	@HideFromJS
	public Map<UUID, FakeServerPlayerDataJS> kjs$getFakePlayerMap() {
		return kjs$fakePlayerMap;
	}

	@Override
	public List<ServerLevelJS> kjs$getAllLevels() {
		return kjs$allLevels;
	}

	@Override
	public LevelJS kjs$getOverworld() {
		if (kjs$overworld == null) {
			kjs$overworld = kjs$wrapMinecraftLevel(kjs$self().overworld());
		}

		return kjs$overworld;
	}

	@Inject(method = "tickServer", at = @At("RETURN"))
	private void kjs$postTickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
		KubeJSServerEventHandler.tickScheduledEvents(System.currentTimeMillis(), kjs$scheduledEvents);
		KubeJSServerEventHandler.tickScheduledEvents(kjs$getOverworld().getTime(), kjs$scheduledTickEvents);
		ServerEvents.TICK.post(new ServerEventJS(kjs$self()));
	}

	@Shadow
	@RemapForJS("isDedicated")
	public abstract boolean isDedicatedServer();

	@Shadow
	@RemapForJS("stop")
	public abstract void close();

	@Override
	public ScheduledEvent kjs$schedule(long timer, IScheduledEventCallback event) {
		var e = new ScheduledEvent(kjs$self(), false, timer, System.currentTimeMillis() + timer, event);
		kjs$scheduledEvents.add(e);
		return e;
	}

	@Override
	public ScheduledEvent kjs$scheduleInTicks(long ticks, IScheduledEventCallback event) {
		var e = new ScheduledEvent(kjs$self(), true, ticks, kjs$getOverworld().getTime() + ticks, event);
		kjs$scheduledTickEvents.add(e);
		return e;
	}
}
