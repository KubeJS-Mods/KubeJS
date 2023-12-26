package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemFrameEntityKJS extends EntityKJS {
	@Override
	default ItemFrame kjs$self() {
		return (ItemFrame) this;
	}

	@Override
	default boolean kjs$isFrame() {
		return true;
	}

	@Override
	@Nullable
	default ItemStack kjs$getItem() {
		var stack = kjs$self().getItem();
		return stack.isEmpty() ? null : stack;
	}
}
