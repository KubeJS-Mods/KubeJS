package dev.latvian.mods.kubejs.item.creativetab;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface CreativeTabContentSupplier {
	record Wrapper(CreativeTabContentSupplier supplier) implements CreativeModeTab.DisplayItemsGenerator {
		@Override
		public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
			List<ItemStack> items = List.of();

			try {
				items = Arrays.stream(supplier.getContent(itemDisplayParameters.hasPermissions())).filter(is -> !is.isEmpty()).toList();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (items.isEmpty()) {
				var is = Items.PAPER.getDefaultInstance();
				is.kjs$setCustomName(Component.literal("Use .content(showRestrictedItems => ['kubejs:example']) to add more items!"));
				output.accept(is);
			} else {
				for (var item : items) {
					output.accept(item);
				}
			}
		}
	}

	CreativeTabContentSupplier DEFAULT = showRestrictedItems -> new ItemStack[0];

	ItemStack[] getContent(boolean showRestrictedItems);
}
