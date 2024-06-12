package dev.latvian.mods.kubejs.helpers;

import dev.latvian.mods.kubejs.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.regex.Pattern;

public enum IngredientHelper {
	INSTANCE;

	public static IngredientHelper get() {
		return INSTANCE;
	}

	public SizedIngredient stack(Ingredient ingredient, int count) {
		return new SizedIngredient(ingredient, count);
	}

	public Ingredient wildcard() {
		return WildcardIngredient.INSTANCE.toVanilla();
	}

	public Ingredient tag(ResourceLocation tag) {
		return Ingredient.of(Tags.item(tag));
	}

	public Ingredient mod(String mod) {
		return new ModIngredient(mod).toVanilla();
	}

	public Ingredient regex(Pattern pattern) {
		return new RegExIngredient(pattern).toVanilla();
	}

	public Ingredient creativeTab(CreativeModeTab tab) {
		return new CreativeTabIngredient(tab).toVanilla();
	}

	public Ingredient subtract(Ingredient base, Ingredient subtracted) {
		return DifferenceIngredient.of(base, subtracted);
	}

	public Ingredient or(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : CompoundIngredient.of(ingredients);
	}

	public Ingredient and(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : IntersectionIngredient.of(ingredients);
	}

	public Ingredient matchComponents(Item item, DataComponentMap map, boolean strong) {
		return new DataComponentIngredient(HolderSet.direct(item.builtInRegistryHolder()), DataComponentPredicate.allOf(map), strong).toVanilla();
	}

	public Ingredient strongComponents(Item item, DataComponentMap map) {
		return matchComponents(item, map, true);
	}

	public Ingredient weakComponents(Item item, DataComponentMap map) {
		return matchComponents(item, map, false);
	}

	public boolean isWildcard(Ingredient ingredient) {
		return ingredient.getCustomIngredient() == WildcardIngredient.INSTANCE;
	}
}