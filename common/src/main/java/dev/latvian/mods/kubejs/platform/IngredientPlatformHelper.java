package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.InputItem;
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

	default InputItem stack(Ingredient ingredient, int count) {
		return InputItem.of(ingredient, count);
	}

	Ingredient wildcard();

	Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate);

	Ingredient custom(Ingredient parent, @Nullable UUID uuid);

	default Ingredient tag(String tag) {
		return Ingredient.of(Tags.item(UtilsJS.getMCID(null, tag)));
	}

	Ingredient mod(String mod);

	Ingredient regex(Pattern pattern);

	Ingredient creativeTab(CreativeModeTab tab);

	Ingredient subtract(Ingredient base, Ingredient subtracted);

	Ingredient or(Ingredient[] ingredients);

	Ingredient and(Ingredient[] ingredients);

	Ingredient strongNBT(ItemStack item);

	Ingredient weakNBT(ItemStack item);

	boolean isWildcard(Ingredient ingredient);
}
