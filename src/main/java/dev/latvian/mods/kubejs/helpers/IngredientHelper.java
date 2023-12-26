package dev.latvian.mods.kubejs.helpers;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.ingredient.CreativeTabIngredient;
import dev.latvian.mods.kubejs.recipe.ingredient.ModIngredient;
import dev.latvian.mods.kubejs.recipe.ingredient.RegExIngredient;
import dev.latvian.mods.kubejs.recipe.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public enum IngredientHelper {
	INSTANCE;

	public static IngredientHelper get() {
		return INSTANCE;
	}

	public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, KubeJS.MOD_ID);
	public static final Supplier<IngredientType<WildcardIngredient>> WILDCARD = INGREDIENT_TYPES.register("wildcard", () -> new IngredientType<>(WildcardIngredient.CODEC));
	public static final Supplier<IngredientType<ModIngredient>> MOD = INGREDIENT_TYPES.register("mod", () -> new IngredientType<>(ModIngredient.CODEC));
	public static final Supplier<IngredientType<RegExIngredient>> REGEX = INGREDIENT_TYPES.register("regex", () -> new IngredientType<>(RegExIngredient.CODEC));
	public static final Supplier<IngredientType<CreativeTabIngredient>> CREATIVE_TAB = INGREDIENT_TYPES.register("creative_tab", () -> new IngredientType<>(CreativeTabIngredient.CODEC));

	public InputItem stack(Ingredient ingredient, int count) {
		return InputItem.of(ingredient, count);
	}

	public static void register(IEventBus bus) {
		INGREDIENT_TYPES.register(bus);
	}

	public Ingredient wildcard() {
		return WildcardIngredient.INSTANCE;
	}

	public Ingredient tag(String tag) {
		return Ingredient.of(Tags.item(UtilsJS.getMCID(null, tag)));
	}

	public Ingredient mod(String mod) {
		return new ModIngredient(mod);
	}

	public Ingredient regex(Pattern pattern) {
		return new RegExIngredient(pattern);
	}

	public Ingredient creativeTab(CreativeModeTab tab) {
		return new CreativeTabIngredient(tab);
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

	public Ingredient strongNBT(ItemStack item) {
		return NBTIngredient.of(true, item.copy());
	}

	public Ingredient weakNBT(ItemStack item) {
		return item.hasTag() ? NBTIngredient.of(false, item.copy()) : item.kjs$asIngredient();
	}

	public boolean isWildcard(Ingredient ingredient) {
		return ingredient == WildcardIngredient.INSTANCE;
	}
}
