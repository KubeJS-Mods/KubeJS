package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.client.KubeSessionData;

public interface ClientPacketListenerKJS {
	default KubeSessionData kjs$sessionData() {
		throw new NoMixinException();
	}
}
