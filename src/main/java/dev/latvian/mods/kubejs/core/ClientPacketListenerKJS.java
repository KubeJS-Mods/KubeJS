package dev.latvian.mods.kubejs.core;

import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.Mutable;

public interface ClientPacketListenerKJS {
	default Mutable<ResourceLocation> kjs$activePostShader() {
		throw new NoMixinException();
	}
}
