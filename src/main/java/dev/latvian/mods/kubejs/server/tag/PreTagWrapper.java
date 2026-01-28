package dev.latvian.mods.kubejs.server.tag;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PreTagWrapper extends TagWrapper {
	public record AddAction(Identifier tag, Object[] filters) implements Consumer<TagKubeEvent> {
		@Override
		public void accept(TagKubeEvent e) {
			e.add(tag, filters);
		}
	}

	public record RemoveAction(Identifier tag, Object[] filters) implements Consumer<TagKubeEvent> {
		@Override
		public void accept(TagKubeEvent e) {
			e.remove(tag, filters);
		}
	}

	public record RemoveAllAction(Identifier tag) implements Consumer<TagKubeEvent> {
		@Override
		public void accept(TagKubeEvent e) {
			e.removeAll(tag);
		}
	}

	public final PreTagKubeEvent preEvent;
	public final Identifier id;

	public PreTagWrapper(PreTagKubeEvent e, Identifier i) {
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
	public List<Identifier> getObjectIds() {
		preEvent.invalid = true;
		return new ArrayList<>(0);
	}
}