package dev.latvian.mods.kubejs.server.tag;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PreTagWrapper extends TagWrapper {
	public record AddAction(ResourceLocation tag, Object[] filters) implements Consumer<TagEventJS> {
		@Override
		public void accept(TagEventJS e) {
			e.add(tag, filters);
		}
	}

	public record RemoveAction(ResourceLocation tag, Object[] filters) implements Consumer<TagEventJS> {
		@Override
		public void accept(TagEventJS e) {
			e.remove(tag, filters);
		}
	}

	public record RemoveAllAction(ResourceLocation tag) implements Consumer<TagEventJS> {
		@Override
		public void accept(TagEventJS e) {
			e.removeAll(tag);
		}
	}

	public final PreTagEventJS preEvent;
	public final ResourceLocation id;

	public PreTagWrapper(PreTagEventJS e, ResourceLocation i) {
		super(e, i, null);
		preEvent = e;
		id = i;
	}

	@Override
	public TagWrapper add(Object... filters) {
		preEvent.actions.add(new AddAction(id, filters));
		return this;
	}

	@Override
	public TagWrapper remove(Object... filters) {
		preEvent.actions.add(new RemoveAction(id, filters));
		return this;
	}

	@Override
	public TagWrapper removeAll() {
		preEvent.actions.add(new RemoveAllAction(id));
		return this;
	}

	@Override
	public List<ResourceLocation> getObjectIds() {
		preEvent.invalid = true;
		return new ArrayList<>(0);
	}
}