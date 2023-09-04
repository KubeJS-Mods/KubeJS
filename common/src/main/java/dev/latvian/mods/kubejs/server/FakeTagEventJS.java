package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FakeTagEventJS extends EventJS {
	public class FakeTagWrapper {
		public final ResourceLocation id;

		private FakeTagWrapper(ResourceLocation i) {
			id = i;
			invalid = false;
		}

		@Override
		public String toString() {
			return "<%s / %s>".formatted(getType(), id);
		}

		public FakeTagWrapper add(String... ids) {
			actions.add(e -> e.get(id).add(ids));
			return this;
		}

		public FakeTagWrapper remove(String... ids) {
			actions.add(e -> e.remove(id).add(ids));
			return this;
		}

		public FakeTagWrapper removeAll() {
			actions.add(e -> e.get(id).removeAll());
			return this;
		}

		public Collection<ResourceLocation> getObjectIds() {
			invalid = true;
			return Set.of();
		}
	}

	public final RegistryInfo registry;
	public final Map<ResourceLocation, FakeTagWrapper> tags;
	public final List<Consumer<TagEventJS<?>>> actions;
	public boolean invalid;

	public FakeTagEventJS(RegistryInfo registry) {
		this.registry = registry;
		this.tags = new ConcurrentHashMap<>();
		this.actions = new ArrayList<>();
	}

	public ResourceLocation getType() {
		return registry.key.location();
	}

	public void post() {
		ServerEvents.TAGS.post(this, registry.key);
	}

	public FakeTagWrapper get(ResourceLocation id) {
		return tags.computeIfAbsent(id, FakeTagWrapper::new);
	}

	public FakeTagWrapper add(ResourceLocation tag, String... ids) {
		return get(tag).add(ids);
	}

	public FakeTagWrapper remove(ResourceLocation tag, String... ids) {
		return get(tag).remove(ids);
	}

	public FakeTagWrapper removeAll(ResourceLocation tag) {
		return get(tag).removeAll();
	}

	public void removeAllTagsFrom(String... ignored) {
		actions.add(e -> e.removeAllTagsFrom(ignored));
	}
}