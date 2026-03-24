package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagLoader;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface TagLoaderKJS<T> {
	default void kjs$customTags(Map<Identifier, List<TagLoader.EntryWithSource>> map) {
		var ssm = kjs$getServerScriptManager();
		var reg = kjs$getRegistry();

		if (ssm == null || reg == null) {
			return;
		}

		var event = TagKubeEvent.fromRegistry(reg, map);

		var objStorage = RegistryObjectStorage.of(reg.key());
		for (var builder : objStorage.objects.values()) {
			for (var s : builder.defaultTags) {
				event.add(s, builder.id);
			}
		}

		for (var e : ssm.serverRegistryTags.entrySet()) {
			for (var tag : e.getValue()) {
				event.add(tag, e.getKey());
			}
		}

		ServerEvents.TAGS.post(event, reg.key());

		// TODO: statistics? i used to like the "tag event posted; x added, y removed in z milliseconds"
		map.clear();
		event.build(map::put);
	}

	void kjs$init(@Nullable ServerScriptManager serverScriptManager, Registry<T> registry);

	@Nullable
	ServerScriptManager kjs$getServerScriptManager();

	@Nullable
	Registry<T> kjs$getRegistry();
}
