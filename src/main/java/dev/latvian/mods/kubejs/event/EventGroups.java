package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;

import java.util.LinkedHashMap;
import java.util.Map;

public record EventGroups(Map<String, EventGroup> map) implements EventGroupRegistry {
	public static final Lazy<EventGroups> ALL = Lazy.of(() -> {
		var groups = new EventGroups(new LinkedHashMap<>());
		KubeJSPlugins.forEachPlugin(p -> p.registerEvents(groups));
		return groups;
	});

	@Override
	public void register(EventGroup group) {
		map.put(group.name, group);
	}
}
