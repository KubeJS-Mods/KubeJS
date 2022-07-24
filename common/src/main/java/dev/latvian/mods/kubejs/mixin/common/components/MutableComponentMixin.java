package dev.latvian.mods.kubejs.mixin.common.components;

import dev.latvian.mods.kubejs.core.ComponentKJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mixin(MutableComponent.class)
@RemapPrefixForJS("kjs$")
public abstract class MutableComponentMixin implements ComponentKJS {

	// hidden to avoid ambiguity, the type wrapper should wrap strings to TextComponent anyways
	@HideFromJS
	@Shadow
	public abstract MutableComponent append(String string);

	@Override
	public Iterator<Component> iterator() {
		if (!kjs$hasSiblings()) {
			return UtilsJS.cast(List.of(kjs$self()).iterator());
		}

		List<Component> list = new LinkedList<>();
		list.add(kjs$self());

		for (var child : getSiblings()) {
			if (child instanceof ComponentKJS wrapped) {
				wrapped.forEach(list::add);
			} else {
				list.add(child);
			}
		}

		return list.iterator();
	}
}
