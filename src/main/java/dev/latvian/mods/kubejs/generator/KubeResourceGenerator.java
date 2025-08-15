package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface KubeResourceGenerator extends KubeEvent {
	RegistryAccessContainer getRegistries();

	void add(GeneratedData data);

	@Nullable
	GeneratedData getGenerated(ResourceLocation id);

	<R, T> void dataMap(DataMapType<R, T> type, Consumer<BiConsumer<ResourceLocation, T>> consumer);

	default void flush() {
	}

	default void text(ResourceLocation id, String content) {
		add(new GeneratedData(id, () -> content.getBytes(StandardCharsets.UTF_8)));
	}

	default void json(ResourceLocation id, JsonElement json) {
		add(GeneratedData.json(id, () -> json));
	}
}
