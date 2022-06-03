package dev.latvian.mods.kubejs.mixin.common.components;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.ComponentKJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mixin(MutableComponent.class)
public interface MutableComponentMixin extends Component, ComponentKJS {

	// hidden to avoid ambiguity, the type wrapper should wrap strings to TextComponent anyways
	@HideFromJS
	@Shadow
	MutableComponent append(String string);

	@Override
	@Shadow
	MutableComponent withStyle(ChatFormatting... args);

	@Override
	@Shadow
	MutableComponent setStyle(Style arg);

	@Override
	default Iterator<Component> iterator() {
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

	default boolean hasStyle() {
		return getStyle() != null && !getStyle().isEmpty();
	}

	default boolean hasSiblings() {
		return !getSiblings().isEmpty();
	}

	default MutableComponent self() {
		return (MutableComponent) this;
	}

	// These following methods only exist for interoperability with old scripts using the Text class
	// region Deprecated
	@Deprecated(forRemoval = true)
	default MutableComponent rawComponent() {
		KubeJS.LOGGER.warn("Using rawComponent() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return self();
	}

	@Deprecated(forRemoval = true)
	default MutableComponent rawCopy() {
		KubeJS.LOGGER.warn("Using rawCopy() is deprecated, since components no longer need to be wrapped to Text! Use copy() instead.");
		return copy();
	}

	@Deprecated(forRemoval = true)
	default Component component() {
		KubeJS.LOGGER.warn("Using component() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
		return self();
	}
	// endregion Deprecated

}
