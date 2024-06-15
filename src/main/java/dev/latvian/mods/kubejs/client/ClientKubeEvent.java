package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.client.Minecraft;

public interface ClientKubeEvent extends KubeEvent {
	default Minecraft getClient() {
		return Minecraft.getInstance();
	}
}
