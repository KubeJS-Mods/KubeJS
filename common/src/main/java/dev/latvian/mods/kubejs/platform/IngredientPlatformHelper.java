package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface IngredientPlatformHelper {
	Lazy<IngredientPlatformHelper> INSTANCE = Lazy.serviceLoader(IngredientPlatformHelper.class);

	static IngredientPlatformHelper get() {
		return INSTANCE.get();
	}

	Ingredient stack(Ingredient ingredient, int count);

	Ingredient wildcard();

	Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate);

	Ingredient custom(Ingredient parent, @Nullable UUID uuid);

	default Ingredient tag(String tag) {
		var t = Tags.item(UtilsJS.getMCID(tag));

		if (RecipeJS.itemErrors && TagContext.INSTANCE.getValue().isEmpty(t)) {
			throw new RecipeExceptionJS("Tag %s doesn't contain any items!".formatted(this)).error();
		}

		return Ingredient.of(t);
	}

	Ingredient mod(String mod);

	Ingredient regex(Pattern pattern);

	Ingredient creativeTab(CreativeModeTab tab);

	Ingredient subtract(Ingredient base, Ingredient subtracted);

	Ingredient or(Ingredient[] ingredients);

	Ingredient and(Ingredient[] ingredients);

	Ingredient strongNBT(ItemStack item);

	Ingredient weakNBT(ItemStack item);
}
