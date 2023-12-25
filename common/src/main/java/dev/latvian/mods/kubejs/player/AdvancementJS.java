package dev.latvian.mods.kubejs.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class AdvancementJS {
	public final AdvancementNode node;

	public final AdvancementHolder holder;
	public final Advancement advancement;

	public AdvancementJS(AdvancementNode node) {
		this.node = node;
		this.holder = node.holder();
		this.advancement = node.advancement();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof AdvancementJS a && node.equals(a.node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	public ResourceLocation id() {
		return getId();
	}

	public ResourceLocation getId() {
		return node.holder().id();
	}

	@Nullable
	public AdvancementJS getParent() {
		var parent = node.parent();
		return parent == null ? null : new AdvancementJS(parent);
	}

	public Set<AdvancementJS> getChildren() {
		Set<AdvancementJS> set = new LinkedHashSet<>();

		for (var a : node.children()) {
			set.add(new AdvancementJS(a));
		}

		return set;
	}

	public void addChild(AdvancementJS a) {
		node.addChild(a.node);
	}

	public Component getDisplayText() {
		return advancement.name().orElse(Component.empty());
	}

	public boolean hasDisplay() {
		return advancement.display().isPresent();
	}

	public Component getTitle() {
		return advancement.display().map(DisplayInfo::getTitle).orElse(Component.empty());
	}

	public Component getDescription() {
		return advancement.display().map(DisplayInfo::getDescription).orElse(Component.empty());
	}

	@Nullable
	public DisplayInfo getDisplay() {
		return advancement.display().orElse(null);
	}
}