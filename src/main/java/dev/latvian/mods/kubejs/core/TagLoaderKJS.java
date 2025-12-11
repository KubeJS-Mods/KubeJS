package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.server.tag.TagEventFilter;
import dev.latvian.mods.kubejs.server.tag.TagKubeEvent;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TagLoaderKJS<T> {
	default void kjs$customTags(ReloadableServerResourcesKJS kjs$resources, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		var reg = kjs$getRegistry();

		if (reg == null) {
			return;
		}

		var objStorage = RegistryObjectStorage.of((ResourceKey) reg.key());

		boolean hasDefaultTags = false;

		for (var builder : (Collection<BuilderBase<?>>) objStorage.objects.values()) {
			if (!builder.defaultTags.isEmpty()) {
				hasDefaultTags = true;
				break;
			}
		}

		hasDefaultTags |= !kjs$getResources().kjs$getServerScriptManager().serverRegistryTags.isEmpty();

		if (hasDefaultTags || ServerEvents.TAGS.hasListeners(objStorage.key)) {
			var preEvent = kjs$getResources().kjs$getServerScriptManager().preTagEvents.get(reg.key());

			var event = new TagKubeEvent(objStorage.key, reg);

			for (var entry : map.entrySet()) {
				var w = new TagWrapper(event, entry.getKey(), entry.getValue());
				event.tags.put(w.id, w);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("Tags %s/#%s; %d".formatted(objStorage, w.id, w.entries.size()));
				}
			}

			for (var builder : (Collection<BuilderBase<?>>) objStorage.objects.values()) {
				for (var s : builder.defaultTags) {
					event.add(s, new TagEventFilter.ID(builder.id));
				}
			}

			for (var e : kjs$getResources().kjs$getServerScriptManager().serverRegistryTags.entrySet()) {
				for (var tag : e.getValue()) {
					event.add(tag, new TagEventFilter.ID(e.getKey()));
				}
			}

			if (preEvent == null) {
				ServerEvents.TAGS.post(event, objStorage.key);
			} else {
				for (var a : preEvent.actions) {
					a.accept(event);
				}
			}

			map.clear();

			for (var entry : event.tags.entrySet()) {
				map.put(entry.getKey(), entry.getValue().entries);
			}

			if (event.totalAdded > 0 || event.totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.info("[%s] Found %d tags, added %d objects, removed %d objects".formatted(objStorage, event.tags.size(), event.totalAdded, event.totalRemoved));
			}
		}

		kjs$resources.kjs$getServerScriptManager().getRegistries().cacheTags(reg, map);
	}

	void kjs$init(ReloadableServerResourcesKJS resources, Registry<T> registry);

	ReloadableServerResourcesKJS kjs$getResources();

	@Nullable
	Registry<T> kjs$getRegistry();
}