package dev.latvian.mods.kubejs.client.icon;

@FunctionalInterface
public interface KubeIconTypeRegistry {
	void register(KubeIconType<?> type);
}
