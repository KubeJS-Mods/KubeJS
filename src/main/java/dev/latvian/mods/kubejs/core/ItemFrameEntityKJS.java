package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ItemFrameEntityKJS extends EntityKJS {
	@Override
	@HideFromJS
	default ItemFrame kjs$self() {
		return (ItemFrame) this;
	}

	@Override
	@ThisIs(ItemFrame.class)
	@Info("Checks if the entity is an item frame entity.")
	default boolean kjs$isFrame() {
		return true;
	}

	@Override
	@Nullable
	@Info("""
		Gets the item stack corresponding to the item in the item frame.
		Will be `null` if the contained stack is empty.
		""")
	default ItemStack kjs$getItem() {
		var stack = kjs$self().getItem();
		return stack.isEmpty() ? null : stack;
	}
}
