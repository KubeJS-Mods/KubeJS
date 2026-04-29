package dev.latvian.mods.kubejs.generator;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/// Base interface for script-driven resource generation.
/// Used by [ServerEvents#GENERATE_DATA] (data pack files) and [ClientEvents#GENERATE_ASSETS] (resource pack files).
///
/// You should probably be using [#json] or [#text] for most cases;
/// [#add] accepts pre-built [GeneratedData] for whatever custom files you might wnat though.
public interface KubeResourceGenerator extends KubeEvent {
	RegistryAccessContainer getRegistries();

	/// Add a generated resource entry as a raw byte array.
	void add(GeneratedData data);

	/// Return a previously added resource by its ID, or `null` if not present.
	@Nullable
	GeneratedData getGenerated(Identifier id);

	/// Build additional data files and clear this generator's state.
	default void flush() {
	}

	/// Add a plain text file with the given id.
	default void text(Identifier id, String content) {
		add(new GeneratedData(id, () -> content.getBytes(StandardCharsets.UTF_8)));
	}

	/// Add a json file with the given id.
	default void json(Identifier id, JsonElement json) {
		add(GeneratedData.json(id, () -> json));
	}
}
