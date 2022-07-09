package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemStackMixin implements ItemStackKJS {
	@Shadow
	private CompoundTag tag;

	@Override
	public void removeTagKJS() {
		tag = null;
	}

	@Override
	@Nullable
	public CompoundTag kjs$getNbt() {
		return tag;
	}

	@Override
	public void kjs$setNbt(@Nullable CompoundTag nbt) {
		tag = nbt;
	}
}
