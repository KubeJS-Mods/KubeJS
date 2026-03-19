package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;

public interface KubeResourceGenerator extends KubeEvent {
	RegistryAccessContainer getRegistries();

	void add(GeneratedData data);

	@Nullable
	GeneratedData getGenerated(Identifier id);

	default void flush() {
	}

	default void text(Identifier id, String content) {
		add(new GeneratedData(id, () -> content.getBytes(StandardCharsets.UTF_8)));
	}

	default void json(Identifier id, JsonElement json) {
		add(GeneratedData.json(id, () -> json));
	}
}
