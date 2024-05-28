package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.mods.kubejs.recipe.special.ShapelessKubeJSRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface KubeJSRecipeSerializers {
	DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, KubeJS.MOD_ID);

	Supplier<RecipeSerializer<?>> SHAPED = REGISTRY.register("shaped", ShapedKubeJSRecipe.SerializerKJS::new);
	Supplier<RecipeSerializer<?>> SHAPELESS = REGISTRY.register("shapeless", ShapelessKubeJSRecipe.SerializerKJS::new);
}
