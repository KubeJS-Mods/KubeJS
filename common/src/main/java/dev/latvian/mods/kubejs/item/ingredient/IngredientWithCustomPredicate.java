package dev.latvian.mods.kubejs.item.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IngredientWithCustomPredicate extends Ingredient {
	public final UUID uuid;
	public final Ingredient ingredient;
	public final Predicate<ItemStack> predicate;

	public IngredientWithCustomPredicate(UUID uuid, Ingredient ingredient, Predicate<ItemStack> predicate) {
		super(Stream.empty());
		this.uuid = uuid;
		this.ingredient = ingredient;
		this.predicate = predicate;
	}
}
