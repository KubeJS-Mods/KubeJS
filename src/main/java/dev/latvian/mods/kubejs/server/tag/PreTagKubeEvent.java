package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PreTagKubeEvent extends TagKubeEvent {
	public static void handle(Map<ResourceKey<?>, PreTagKubeEvent> tagEventHolders) {
		tagEventHolders.clear();

		if (ServerEvents.TAGS.hasListeners()) {
			for (var id : ServerEvents.TAGS.findUniqueExtraIds(ScriptType.SERVER)) {
				var e = new PreTagKubeEvent(id);
				try {
					ServerEvents.TAGS.post(ScriptType.SERVER, id, e);
				} catch (Exception ex) {
					e.invalid = true;

					if (DevProperties.get().logEventErrorStackTrace) {
						KubeJS.LOGGER.warn("Pre Tag event for " + e.registryKey.location() + " failed:");
						ex.printStackTrace();
					}
				}

				if (!e.invalid) {
					tagEventHolders.put(e.registryKey, e);
				}
			}
		}
	}

	public record RemoveAllTagsFromAction(Object[] ignored) implements Consumer<TagKubeEvent> {
		@Override
		public void accept(TagKubeEvent e) {
			e.removeAllTagsFrom(ignored);
		}
	}

	public final Map<ResourceLocation, PreTagWrapper> tags;
	public final List<Consumer<TagKubeEvent>> actions;
	public boolean invalid;

	public PreTagKubeEvent(ResourceKey<?> registryKey) {
		super(registryKey, null);
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