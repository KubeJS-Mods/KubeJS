package dev.latvian.mods.kubejs.mixin.common.components;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.ComponentKJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mixin(MutableComponent.class)
public abstract class MutableComponentMixin implements ComponentKJS {

	// hidden to avoid ambiguity, the type wrapper should wrap strings to TextComponent anyways
	@HideFromJS
	@Shadow
	public abstract MutableComponent append(String string);

	@Override
	public Iterator<Component> iterator() {
		if (!hasSiblings()) {
			return UtilsJS.cast(List.of(self()).iterator());
		}

		List<Component> list = new LinkedList<>();
		list.add(self());

		for (var child : getSiblings()) {
			if (child instanceof ComponentKJS wrapped) {
				wrapped.forEach(list::add);
			} else {
				list.add(child);
			}
		}

		return list.iterator();
	}

	public boolean hasStyle() {
		return getStyle() != null && !getStyle().isEmpty();
	}

	public boolean hasSiblings() {
		return !getSiblings().isEmpty();
	}

	// These following methods only exist for interoperability with old scripts using the Text class
	// region Deprecated
	@Deprecated(forRemoval = true)
	public MutableComponent rawComponent() {
		KubeJS.LOGGER.warn("Using rawComponent() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return self();
	}

	@Deprecated(forRemoval = true)
	public MutableComponent rawCopy() {
		KubeJS.LOGGER.warn("Using rawCopy() is deprecated, since components no longer need to be wrapped to Text! Use copy() instead.");
		return copy();
	}

	@Deprecated(forRemoval = true)
	public Component component() {
		KubeJS.LOGGER.warn("Using component() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return self();
	}
	// endregion Deprecated

}
