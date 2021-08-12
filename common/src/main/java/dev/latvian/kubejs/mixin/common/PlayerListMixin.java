package dev.latvian.kubejs.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.player.CheckPlayerLoginEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	@Shadow
	@Final
	private MinecraftServer server;

	@Inject(method = "canPlayerLogin", at = @At("RETURN"), cancellable = true)
	private void canPlayerLoginKJS(SocketAddress address, GameProfile gameProfile, CallbackInfoReturnable<Component> cir) {
		if (!server.isSingleplayer() && cir.getReturnValue() == null) {
			CheckPlayerLoginEventJS event = new CheckPlayerLoginEventJS(address, gameProfile);
			if (event.post(ScriptType.SERVER, KubeJSEvents.PLAYER_CHECK_LOGIN)) {
				cir.setReturnValue(event.getReason());
			}
		}
	}
}
