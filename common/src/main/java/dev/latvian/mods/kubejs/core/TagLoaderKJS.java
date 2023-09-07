package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.server.tag.TagEventFilter;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TagLoaderKJS<T> {
	default void kjs$customTags(ServerScriptManager ssm, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		TagContext.INSTANCE.setValue(TagContext.EMPTY);
		var reg = kjs$getRegistry();

		if (reg == null) {
			return;
		}

		var regInfo = RegistryInfo.of(reg.key());
		regInfo.vanillaRegistry = reg;

		if (regInfo.hasDefaultTags || ServerEvents.TAGS.hasListeners(reg.key())) {
			var preEvent = ssm.preTagEvents.get(reg.key());

			var event = new TagEventJS(regInfo);

			for (var entry : map.entrySet()) {
				var w = new TagWrapper(event, entry.getKey(), entry.getValue());
				event.tags.put(w.id, w);

				if (ConsoleJS.SERVER.shouldPrintDebug()) {
					ConsoleJS.SERVER.debug("Tags %s/#%s; %d".formatted(regInfo, w.id, w.entries.size()));
				}
			}

			for (var builder : regInfo.objects.values()) {
				for (var s : builder.defaultTags) {
					event.add(s, new TagEventFilter.ID(builder.id));
				}
			}

			if (preEvent == null) {
				ServerEvents.TAGS.post(event, regInfo.key, TagEventJS.TAG_EVENT_HANDLER);
			} else {
				for (var a : preEvent.actions) {
					a.accept(event);
				}
			}

			map.clear();

			for (var entry : event.tags.entrySet()) {
				map.put(entry.getKey(), entry.getValue().entries);
			}

			if (DataExport.export != null) {
				var loc = "tags/" + regInfo + "/";

				for (var entry : map.entrySet()) {
					var list = new ArrayList<String>();

					for (var e : entry.getValue()) {
						list.add(e.entry().toString());
					}

					list.sort(String.CASE_INSENSITIVE_ORDER);
					var arr = new JsonArray();

					for (var e : list) {
						arr.add(e);
					}

					DataExport.export.addJson(loc + entry.getKey() + ".json", arr);
				}
			}

			if (event.totalAdded > 0 || event.totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
				ConsoleJS.SERVER.info("[%s] Found %d tags, added %d objects, removed %d objects".formatted(regInfo, event.tags.size(), event.totalAdded, event.totalRemoved));
			}
		}
	}

	void kjs$setRegistry(Registry<T> registry);

	@Nullable
	Registry<T> kjs$getRegistry();
}