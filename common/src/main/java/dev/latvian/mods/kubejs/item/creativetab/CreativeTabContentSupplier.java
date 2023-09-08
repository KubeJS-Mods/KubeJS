package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface CreativeTabContentSupplier extends CreativeModeTab.DisplayItemsGenerator {
	CreativeTabContentSupplier DEFAULT = showRestrictedItems -> new ItemStack[0];

	ItemStack[] getContent(boolean showRestrictedItems);

	@Override
	default void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
		List<ItemStack> items = List.of();

		try {
			items = Arrays.stream(getContent(itemDisplayParameters.hasPermissions())).map(ItemStackJS::of).filter(is -> !is.isEmpty()).toList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (items.isEmpty()) {
			output.accept(Items.PAPER.getDefaultInstance().kjs$withName(Component.literal("Use .content(showRestrictedItems => ['kubejs:example']) to add more items!")));
		} else {
			for (var item : items) {
				output.accept(item);
			}
		}
	}
}
