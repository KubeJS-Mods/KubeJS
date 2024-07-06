package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ClientPacketListenerKJS;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements ClientPacketListenerKJS {
	@Unique
	private final Mutable<ResourceLocation> kjs$activePostShader = new MutableObject<>(null);

	@Override
	public Mutable<ResourceLocation> kjs$activePostShader() {
		return kjs$activePostShader;
	}
}
