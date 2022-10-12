package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.ingredient.AndIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CustomIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.CustomPredicateIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.IngredientStackImpl;
import dev.latvian.mods.kubejs.platform.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.NotIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.StrongNBTIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.TagIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.WeakNBTIngredient;
import dev.latvian.mods.kubejs.platform.ingredient.WildcardIngredient;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IngredientPlatformHelperImpl implements IngredientPlatformHelper {
	public static void register() {
		CraftingHelper.register(KubeJS.id("stack"), IngredientStackImpl.SERIALIZER);
		CraftingHelper.register(KubeJS.id("wildcard"), WildcardIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("custom"), CustomIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("custom_predicate"), CustomPredicateIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("tag"), TagIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("mod"), ModIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("regex"), RegExIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("creative_tab"), CreativeTabIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("not"), NotIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("and"), AndIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("strong_nbt"), StrongNBTIngredient.SERIALIZER);
		CraftingHelper.register(KubeJS.id("weak_nbt"), WeakNBTIngredient.SERIALIZER);
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
		return new CustomIngredient(predicate);
	}

	@Override
	public Ingredient custom(Ingredient parent, @Nullable UUID uuid) {
		return new CustomPredicateIngredient(parent, uuid);
	}

	@Override
	public Ingredient tag(String tag) {
		return TagIngredient.ofTag(tag);
	}

	@Override
	public Ingredient mod(String mod) {
		return ModIngredient.ofMod(mod);
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
	public Ingredient not(Ingredient ingredient) {
		return new NotIngredient(ingredient);
	}

	@Override
	public Ingredient or(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : CompoundIngredient.of(ingredients);
	}

	@Override
	public Ingredient and(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : new AndIngredient(ingredients);
	}

	@Override
	public Ingredient strongNBT(ItemStack item) {
		return new StrongNBTIngredient(item);
	}

	@Override
	public Ingredient weakNBT(ItemStack item) {
		return new WeakNBTIngredient(item);
	}
}
