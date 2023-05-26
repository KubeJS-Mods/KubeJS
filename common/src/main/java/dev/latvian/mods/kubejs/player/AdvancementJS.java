package dev.latvian.mods.kubejs.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class AdvancementJS {
	public final Advancement advancement;

	public AdvancementJS(Advancement a) {
		advancement = a;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof AdvancementJS && advancement.equals(((AdvancementJS) o).advancement);
	}

	@Override
	public int hashCode() {
		return advancement.hashCode();
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	public ResourceLocation id() {
		return getId();
	}

	public ResourceLocation getId() {
		return advancement.getId();
	}

	@Nullable
	public AdvancementJS getParent() {
		return advancement.getParent() == null ? null : new AdvancementJS(advancement.getParent());
	}

	public Set<AdvancementJS> getChildren() {
		Set<AdvancementJS> set = new LinkedHashSet<>();

		for (var a : advancement.getChildren()) {
			set.add(new AdvancementJS(a));
		}

		return set;
	}

	public void addChild(AdvancementJS a) {
		advancement.addChild(a.advancement);
	}

	public Component getDisplayText() {
		return advancement.getChatComponent();
	}

	public boolean hasDisplay() {
		return advancement.getDisplay() != null;
	}

	public Component getTitle() {
		return advancement.getDisplay() != null ? advancement.getDisplay().getTitle() : Component.empty();
	}

	public Component getDescription() {
		return advancement.getDisplay() != null ? advancement.getDisplay().getDescription() : Component.empty();
	}
}