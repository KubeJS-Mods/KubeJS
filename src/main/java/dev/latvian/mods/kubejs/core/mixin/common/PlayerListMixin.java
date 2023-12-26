package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	/*
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
	*/

	@Inject(method = "respawn", at = @At("RETURN"))
	private void kjs$respawn(ServerPlayer serverPlayer, boolean keepData, CallbackInfoReturnable<ServerPlayer> cir) {
		KubeJSPlayerEventHandler.respawn(serverPlayer, cir.getReturnValue(), keepData);
	}
}
