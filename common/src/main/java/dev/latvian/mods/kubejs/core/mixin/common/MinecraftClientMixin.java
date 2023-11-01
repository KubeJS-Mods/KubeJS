package dev.latvian.mods.kubejs.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.GeneratedClientResourcePack;
import dev.latvian.mods.kubejs.client.ScheduledClientEvent;
import dev.latvian.mods.kubejs.core.MinecraftClientKJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.LinkedList;
import java.util.List;

@Mixin(Minecraft.class)
@RemapPrefixForJS("kjs$")
public abstract class MinecraftClientMixin implements MinecraftClientKJS {
	private final List<ScheduledClientEvent> kjs$scheduledEvents = new LinkedList<>();

	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void kjs$createTitle(CallbackInfoReturnable<String> ci) {
		var s = ClientProperties.get().title;

		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}

	@ModifyExpressionValue(
		method = {"reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", "<init>"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")
	)
	private List<PackResources> kjs$loadPacks(List<PackResources> resources) {
		return GeneratedClientResourcePack.inject(kjs$self(), resources);
	}

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V", shift = At.Shift.AFTER))
	private void kjs$startAttack(CallbackInfoReturnable<Boolean> cir) {
		kjs$startAttack0();
	}

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V", ordinal = 0, shift = At.Shift.AFTER))
	private void kjs$startUseItem(CallbackInfo ci) {
		kjs$startUseItem0();
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void kjs$postTickClient(CallbackInfo ci) {
		if (kjs$self().player != null && ClientEvents.TICK.hasListeners()) {
			ScheduledClientEvent.tickAll(System.currentTimeMillis(), kjs$self().level.getGameTime(), kjs$scheduledEvents);
			ClientEvents.TICK.post(ScriptType.CLIENT, new ClientEventJS());
		}
	}

	@Override
	public ScheduledClientEvent kjs$schedule(TemporalAmount timer, ScheduledClientEvent.Callback event) {
		if (timer instanceof TickDuration duration) {
			var e = new ScheduledClientEvent.InTicks(kjs$self(), duration, kjs$self().level.getGameTime() + duration.ticks(), event);
			kjs$scheduledEvents.add(e);
			return e;
		} else if (timer instanceof Duration duration) {
			var e = new ScheduledClientEvent.InMs(kjs$self(), duration, System.currentTimeMillis() + duration.toMillis(), event);
			kjs$scheduledEvents.add(e);
			return e;
		} else {
			throw new IllegalArgumentException("Unsupported TemporalAmount: " + timer);
		}
	}
}