package dev.latvian.mods.kubejs.ingredient;

import dev.latvian.mods.kubejs.KubeJS;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public interface KubeJSIngredients {
	DeferredRegister<IngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, KubeJS.MOD_ID);

	Supplier<IngredientType<WildcardIngredient>> WILDCARD = REGISTRY.register("wildcard", () -> new IngredientType<>(WildcardIngredient.CODEC));
	Supplier<IngredientType<TagIngredient>> TAG = REGISTRY.register("tag", () -> new IngredientType<>(TagIngredient.CODEC));
	Supplier<IngredientType<NamespaceIngredient>> NAMESPACE = REGISTRY.register("namespace", () -> new IngredientType<>(NamespaceIngredient.CODEC));
	Supplier<IngredientType<RegExIngredient>> REGEX = REGISTRY.register("regex", () -> new IngredientType<>(RegExIngredient.CODEC));
	Supplier<IngredientType<CreativeTabIngredient>> CREATIVE_TAB = REGISTRY.register("creative_tab", () -> new IngredientType<>(CreativeTabIngredient.CODEC));
}
