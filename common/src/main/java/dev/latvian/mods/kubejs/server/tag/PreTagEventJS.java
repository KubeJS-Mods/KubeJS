package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PreTagEventJS extends TagEventJS {
	public static void handle(Map<ResourceKey<?>, PreTagEventJS> tagEventHolders) {
		tagEventHolders.clear();

		if (ServerEvents.TAGS.hasListeners()) {
			for (var id : ServerEvents.TAGS.findUniqueExtraIds(ScriptType.SERVER)) {
				var e = new PreTagEventJS(RegistryInfo.of((ResourceKey) id));
				try {
					ServerEvents.TAGS.post(ScriptType.SERVER, id, e);
				} catch (Exception ex) {
					e.invalid = true;

					if (DevProperties.get().debugInfo) {
						KubeJS.LOGGER.warn("Pre Tag event for " + e.registry + " failed:");
						ex.printStackTrace();
					}
				}

				if (!e.invalid) {
					tagEventHolders.put(e.registry.key, e);
				}
			}
		}
	}

	public record RemoveAllTagsFromAction(Object[] ignored) implements Consumer<TagEventJS> {
		@Override
		public void accept(TagEventJS e) {
			e.removeAllTagsFrom(ignored);
		}
	}

	public final Map<ResourceLocation, PreTagWrapper> tags;
	public final List<Consumer<TagEventJS>> actions;
	public boolean invalid;

	public PreTagEventJS(RegistryInfo registry) {
		super(registry, null);
		this.tags = new ConcurrentHashMap<>();
		this.actions = new ArrayList<>();
	}

	@Override
	protected TagWrapper createTagWrapper(ResourceLocation id) {
		return new PreTagWrapper(this, id);
	}

	@Override
	public void removeAllTagsFrom(Object... ignored) {
		actions.add(new RemoveAllTagsFromAction(ignored));
	}

	@Override
	public Set<ResourceLocation> getElementIds() {
		return Set.of();
	}
}