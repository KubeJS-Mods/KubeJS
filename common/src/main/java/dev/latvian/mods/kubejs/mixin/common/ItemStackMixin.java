package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.ItemStackKJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackKJS {
	@Shadow
	private CompoundTag tag;

	@Override
	public void removeTagKJS() {
		tag = null;
	}
}
