package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public interface IngredientPlatformHelper {
	Ingredient[] EMPTY_ARRAY = new Ingredient[0];
	Ingredient.Value[] EMPTY_VALUES = new Ingredient.Value[0];

	Supplier<IngredientPlatformHelper> INSTANCE = Suppliers.memoize(() -> {
		var serviceLoader = ServiceLoader.load(IngredientPlatformHelper.class);
		return serviceLoader.findFirst().orElseThrow(() -> new RuntimeException("Could not find a IngredientHelper for your platform!"));
	});

	static IngredientPlatformHelper get() {
		return INSTANCE.get();
	}

	IngredientStack stack(Ingredient ingredient, int count);

	Ingredient wildcard();

	Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate);

	Ingredient custom(Ingredient parent, @Nullable UUID uuid);

	Ingredient tag(String tag);

	Ingredient mod(String mod);

	Ingredient regex(Pattern pattern);

	Ingredient creativeTab(CreativeModeTab tab);

	Ingredient not(Ingredient ingredient);

	Ingredient or(Ingredient[] ingredients);

	Ingredient and(Ingredient[] ingredients);

	Ingredient ignoreNBT(Item item);

	Ingredient strongNBT(ItemStack item);

	Ingredient weakNBT(ItemStack item);
}
