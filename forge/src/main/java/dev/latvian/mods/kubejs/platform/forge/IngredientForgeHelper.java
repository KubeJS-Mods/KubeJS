package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.forge.ingredient.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class IngredientForgeHelper implements IngredientPlatformHelper {

	public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, KubeJS.MOD_ID);

	public static final Supplier<IngredientType<WildcardIngredient>> WILDCARD = INGREDIENT_TYPES.register("wildcard", () -> new IngredientType<>(WildcardIngredient.CODEC));
	public static final Supplier<IngredientType<CustomIngredient>> CUSTOM = INGREDIENT_TYPES.register("custom", () -> new IngredientType<>(CustomIngredient.CODEC));
	public static final Supplier<IngredientType<CustomPredicateIngredient>> CUSTOM_PREDICATE = INGREDIENT_TYPES.register("custom_predicate", () -> new IngredientType<>(CustomPredicateIngredient.CODEC));
	public static final Supplier<IngredientType<ModIngredient>> MOD = INGREDIENT_TYPES.register("mod", () -> new IngredientType<>(ModIngredient.CODEC));
	public static final Supplier<IngredientType<RegExIngredient>> REGEX = INGREDIENT_TYPES.register("regex", () -> new IngredientType<>(RegExIngredient.CODEC));
	public static final Supplier<IngredientType<CreativeTabIngredient>> CREATIVE_TAB = INGREDIENT_TYPES.register("creative_tab", () -> new IngredientType<>(CreativeTabIngredient.CODEC));

	public static void register(IEventBus bus) {
		INGREDIENT_TYPES.register(bus);
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
		return new CustomPredicateIngredient(parent, uuid, true);
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
		return DifferenceIngredient.of(base, subtracted);
	}

	@Override
	public Ingredient or(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : CompoundIngredient.of(ingredients);
	}

	@Override
	public Ingredient and(Ingredient[] ingredients) {
		return ingredients.length == 0 ? Ingredient.EMPTY : ingredients.length == 1 ? ingredients[0] : IntersectionIngredient.of(ingredients);
	}

	@Override
	public Ingredient strongNBT(ItemStack item) {
		return NBTIngredient.of(true, item.copy());
	}

	@Override
	public Ingredient weakNBT(ItemStack item) {
		return item.hasTag() ? NBTIngredient.of(false, item.copy()) : item.kjs$asIngredient();
	}

	@Override
	public boolean isWildcard(Ingredient ingredient) {
		return ingredient == WildcardIngredient.INSTANCE;
	}
}
