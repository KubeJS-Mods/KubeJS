package dev.latvian.mods.kubejs.registry;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ServerRegistryKubeEvent<T> implements KubeEvent {
	public final KubeDataGenerator generator;
	public final ResourceKey<Registry<?>> key;
	public final DynamicOps<JsonElement> jsonOps;
	public final Codec<T> codec;

	public ServerRegistryKubeEvent(KubeDataGenerator generator, ResourceKey<Registry<?>> key, DynamicOps<JsonElement> jsonOps, Codec<T> codec) {
		this.generator = generator;
		this.key = key;
		this.jsonOps = jsonOps;
		this.codec = codec;
	}

	public void createFromJson(String id, JsonElement json) {
		var id1 = ID.kjs(id);
		var k = key.location();

		if (k.getNamespace().equals("minecraft")) {
			generator.json(ResourceLocation.fromNamespaceAndPath(id1.getNamespace(), k.getPath() + "/" + id1.getPath()), json);
		} else {
			generator.json(ResourceLocation.fromNamespaceAndPath(id1.getNamespace(), k.getNamespace() + "/" + k.getPath() + "/" + id1.getPath()), json);
		}
	}
}