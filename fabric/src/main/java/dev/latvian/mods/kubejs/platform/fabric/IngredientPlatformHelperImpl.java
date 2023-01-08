package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CustomIngredientWithParent;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.IngredientStackImpl;
import dev.latvian.mods.kubejs.platform.fabric.ingredient.ModIngredient;
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

public class IngredientPlatformHelperImpl implements IngredientPlatformHelper {
	public static void register() {
		CustomIngredientSerializer.register(IngredientStackImpl.SERIALIZER);
		CustomIngredientSerializer.register(WildcardIngredient.SERIALIZER);
		CustomIngredientSerializer.register(CustomIngredientWithParent.SERIALIZER);
		CustomIngredientSerializer.register(CustomPredicateIngredient.SERIALIZER);
		CustomIngredientSerializer.register(ModIngredient.SERIALIZER);
		CustomIngredientSerializer.register(RegExIngredient.SERIALIZER);
		CustomIngredientSerializer.register(CreativeTabIngredient.SERIALIZER);
	}

	@Override
	public Ingredient stack(Ingredient ingredient, int count) {
		return new IngredientStackImpl(ingredient, count).toVanilla();
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
		return DefaultCustomIngredients.nbt(item, true);
	}

	@Override
	public Ingredient weakNBT(ItemStack item) {
		return item.getTag() == null ? item.kjs$asIngredient() : DefaultCustomIngredients.nbt(item, false);
	}
}
