package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CustomIngredientWithParent;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.PatchedNbtIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.WildcardIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IngredientFabricHelper implements IngredientPlatformHelper {
	public static void register() {
		CustomIngredientSerializer.register(WildcardIngredient.SERIALIZER);
		CustomIngredientSerializer.register(CustomIngredientWithParent.SERIALIZER);
		CustomIngredientSerializer.register(CustomPredicateIngredient.SERIALIZER);
		CustomIngredientSerializer.register(ModIngredient.SERIALIZER);
		CustomIngredientSerializer.register(RegExIngredient.SERIALIZER);
		CustomIngredientSerializer.register(CreativeTabIngredient.SERIALIZER);
		CustomIngredientSerializer.register(PatchedNbtIngredient.SERIALIZER); // Temp until fabric PR is merged
	}

	@Override
	public Ingredient wildcard() {
		return WildcardIngredient.VANILLA_INSTANCE;
	}

	@Override
	public Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
		return new CustomIngredientWithParent(parent, predicate).toVanilla();
	}

	@Override
	public Ingredient custom(Ingredient parent, @Nullable UUID uuid) {
		return new CustomPredicateIngredient(parent, uuid).toVanilla();
	}

	@Override
	public Ingredient mod(String mod) {
		return new ModIngredient(mod).toVanilla();
	}

	@Override
	public Ingredient regex(Pattern pattern) {
		return new RegExIngredient(pattern).toVanilla();
	}

	@Override
	public Ingredient creativeTab(CreativeModeTab tab) {
		return new CreativeTabIngredient(tab).toVanilla();
	}

	@Override
	public Ingredient subtract(Ingredient base, Ingredient subtracted) {
		return DefaultCustomIngredients.difference(base, subtracted);
	}

	@Override
	public Ingredient or(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : DefaultCustomIngredients.any(ingredients);
	}

	@Override
	public Ingredient and(Ingredient[] ingredients) {
		return ingredients.length == 0 ? WildcardIngredient.VANILLA_INSTANCE : ingredients.length == 1 ? ingredients[0] : DefaultCustomIngredients.all(ingredients);
	}

	@Override
	public Ingredient strongNBT(ItemStack item) {
		return new PatchedNbtIngredient(item.kjs$asIngredient(), item.getTag(), true).toVanilla();
	}

	@Override
	public Ingredient weakNBT(ItemStack item) {
		return item.getTag() == null ? item.kjs$asIngredient() : new PatchedNbtIngredient(item.kjs$asIngredient(), item.getTag(), false).toVanilla();
	}

	@Override
	public boolean isWildcard(Ingredient ingredient) {
		return ingredient.getCustomIngredient() == WildcardIngredient.INSTANCE;
	}
}
