package dev.latvian.mods.kubejs.ingredient;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import java.util.stream.Stream;

public interface KubeJSIngredient extends ICustomIngredient, ItemPredicate {
	@Override
	boolean test(ItemStack stack);


	@Override
	default Stream<Holder<Item>> items() {
		return ItemWrapper.getList().stream().filter(template -> test(template.create())).map(template -> template.item());
	}

	@Override
	default boolean isSimple() {
		return CommonProperties.get().serverOnly;
	}

	// we do need this override since ICustomIngredient has default false
	// and all of our ingredients are safe
	@Override
	default boolean kjs$canBeUsedForMatching() {
		return true;
	}
}
