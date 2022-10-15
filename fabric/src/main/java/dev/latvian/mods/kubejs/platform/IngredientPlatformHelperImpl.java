package dev.latvian.mods.kubejs.platform;

import com.faux.ingredientextension.api.ingredient.IngredientHelper;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.ingredient.AndIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CustomIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.IngredientStackImpl;
import dev.latvian.mods.kubejs.platform.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.OrIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.StrongNBTIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.SubIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.WeakNBTIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.WildcardIngredient;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IngredientPlatformHelperImpl implements IngredientPlatformHelper {
	public static void register() {
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("stack"), IngredientStackImpl.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("wildcard"), WildcardIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("custom"), CustomIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("custom_predicate"), CustomPredicateIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("mod"), ModIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("regex"), RegExIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("creative_tab"), CreativeTabIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("sub"), SubIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("or"), OrIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("and"), AndIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("strong_nbt"), StrongNBTIngredient.SERIALIZER);
		Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, KubeJS.id("weak_nbt"), WeakNBTIngredient.SERIALIZER);
	}

	@Override
	public Ingredient stack(Ingredient ingredient, int count) {
		return new IngredientStackImpl(ingredient, count);
	}

	@Override
	public Ingredient wildcard() {
		return WildcardIngredient.INSTANCE;
	}

	@Override
	public Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
		return new CustomIngredient(parent, predicate);
	}

	@Override
	public Ingredient custom(Ingredient parent, @Nullable UUID uuid) {
		return new CustomPredicateIngredient(parent, uuid);
	}

	@Override
	public Ingredient mod(String mod) {
		return new ModIngredient(mod);
	}

	@Override
	public Ingredient regex(Pattern pattern) {
		return new RegExIngredient(pattern);
	}

	@Override
	public Ingredient creativeTab(CreativeModeTab tab) {
		return new CreativeTabIngredient(tab);
	}

	@Override
	public Ingredient subtract(Ingredient base, Ingredient subtracted) {
		return new SubIngredient(base, subtracted);
	}

	@Override
	public Ingredient or(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : new OrIngredient(ingredients);
	}

	@Override
	public Ingredient and(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : new AndIngredient(ingredients);
	}

	@Override
	public Ingredient strongNBT(ItemStack item) {
		return new StrongNBTIngredient(item.getItem(), item.getTag());
	}

	@Override
	public Ingredient weakNBT(ItemStack item) {
		return item.getTag() == null ? item.kjs$asIngredient() : new WeakNBTIngredient(item.getItem(), item.getTag());
	}
}
