package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.client.KubeSessionData;
import dev.latvian.mods.kubejs.core.ClientPacketListenerKJS;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements ClientPacketListenerKJS {
	@Unique
	private final KubeSessionData kjs$sessionData = new KubeSessionData();

	@Override
	public KubeSessionData kjs$sessionData() {
		return kjs$sessionData;
	}
}
