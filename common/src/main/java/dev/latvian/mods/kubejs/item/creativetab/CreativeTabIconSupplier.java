package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

@FunctionalInterface
public interface CreativeTabIconSupplier extends Supplier<ItemStack> {
	CreativeTabIconSupplier DEFAULT = () -> ItemStack.EMPTY;

	ItemStack getIcon();

	@Override
	default ItemStack get() {
		try {
			var i = ItemStackJS.of(getIcon());
			return i.isEmpty() ? Items.PURPLE_DYE.getDefaultInstance() : i;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return Items.PURPLE_DYE.getDefaultInstance();
	}
}
