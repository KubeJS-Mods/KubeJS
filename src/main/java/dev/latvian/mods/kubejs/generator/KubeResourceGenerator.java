package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;

public interface KubeResourceGenerator extends KubeEvent {
	void add(GeneratedData data);

	default void text(ResourceLocation id, String content) {
		add(new GeneratedData(id, () -> content.getBytes(StandardCharsets.UTF_8)));
	}

	default void json(ResourceLocation id, JsonElement json) {
		add(GeneratedData.json(id, () -> json));
	}
}
