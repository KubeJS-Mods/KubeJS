package dev.latvian.mods.kubejs.core.mixin;

import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
	// FIXME
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
}
