package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.error.EmptyTagTargetException;
import dev.latvian.mods.kubejs.event.EventExceptionHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagLoader.EntryWithSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class TagKubeEvent implements KubeEvent {
	public static final EventExceptionHandler TAG_EVENT_HANDLER = (event, container, ex) -> {
		if (ex instanceof IllegalStateException) {
			var stacktrace = ex.getStackTrace();
			if (stacktrace.length > 0) {
				if (stacktrace[0].toString().contains("dev.latvian.mods.rhino.ScriptRuntime.doTopCall")) {
					var error = ex.getCause() == null ? ex : ex.getCause();
					ScriptType.SERVER.console.error("IllegalStateException was thrown during tag event in script %s:%d, this is most likely due to a concurrency bug in Rhino! While we are working on a fix for this issue, you may manually work around it by reloading the server again (e.g. by using /reload command).".formatted(container.source, container.line), error);
					return null;
				}
			}
		} else if (ex instanceof EmptyTagTargetException) {
			ScriptType.SERVER.console.error(ex.getMessage() + " (at %s:%d)".formatted(container.source, container.line));
			return null;
		}
		return ex;
	};

	public static final String SOURCE = "KubeJS Custom Tags";

	public final ResourceKey<?> registryKey;
	public final Map<Identifier, TagWrapper> tags;
	final Map<Identifier, TagEventBuilder> builders;
	final Set<Identifier> elementIds;

	private TagKubeEvent(Registry<?> vanillaRegistry, Map<Identifier, List<EntryWithSource>> map) {
		this.registryKey = vanillaRegistry.key();
		this.tags = new ConcurrentHashMap<>();
		this.builders = new HashMap<>();
		this.elementIds = vanillaRegistry.keySet();

		for (var entry : map.entrySet()) {
			builders.put(entry.getKey(), new TagEventBuilder(entry.getValue()));
		}
	}

	public static TagKubeEvent fromRegistry(Registry<?> vanillaRegistry, Map<Identifier, List<EntryWithSource>> map) {
		return new TagKubeEvent(vanillaRegistry, map);
	}

	public Identifier getType() {
		return registryKey.identifier();
	}

	public TagWrapper get(Identifier id) {
		return tags.computeIfAbsent(id, this::createTagWrapper);
	}

	protected TagWrapper createTagWrapper(Identifier id) {
		return new TagWrapper(this, id);
	}

	TagEventBuilder getOrCreateBuilder(Identifier id) {
		return builders.computeIfAbsent(id, _ -> new TagEventBuilder(List.of()));
	}

	public TagWrapper add(Identifier tag, Object... filters) {
		return get(tag).add(filters);
	}

	public TagWrapper remove(Identifier tag, Object... filters) {
		return get(tag).remove(filters);
	}

	public TagWrapper removeAll(Identifier tag) {
		return get(tag).removeAll();
	}

	public Set<Identifier> getElementIds() {
		return elementIds;
	}

	boolean hasElement(Identifier id) {
		return elementIds.contains(id);
	}

	public void build(BiConsumer<Identifier, List<EntryWithSource>> output) {
		for (var entry : builders.entrySet()) {
			output.accept(entry.getKey(), entry.getValue().buildEntries());
		}
	}
}
