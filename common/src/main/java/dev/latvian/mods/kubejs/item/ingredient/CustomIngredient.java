package dev.latvian.mods.kubejs.item.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author LatvianModder
 */
public class CustomIngredient extends Ingredient {
	private final Predicate<ItemStack> predicate;

	public CustomIngredient(Predicate<ItemStack> predicate) {
		super(Stream.empty());
		this.predicate = predicate;
	}

	@Override
	public boolean test(ItemStack stack) {
		return predicate.test(stack);
	}
}
