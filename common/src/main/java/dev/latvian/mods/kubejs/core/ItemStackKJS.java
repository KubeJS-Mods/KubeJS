package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface ItemStackKJS extends AsKJS<ItemStackJS> {
	@Override
	default ItemStackJS asKJS() {
		return ItemStackJS.of(this);
	}

	void removeTagKJS();

	@Nullable
	CompoundTag kjs$getNbt();

	void kjs$setNbt(@Nullable CompoundTag nbt);
}
