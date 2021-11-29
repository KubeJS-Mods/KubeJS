package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.mods.kubejs.recipe.special.ShapelessKubeJSRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class KubeJSRecipeEventHandler {
	public static Supplier<RecipeSerializer<?>> SHAPED;
	public static Supplier<RecipeSerializer<?>> SHAPELESS;

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}
	}

	private static void registry() {
		SHAPED = KubeJSRegistries.recipeSerializers().register(new ResourceLocation(KubeJS.MOD_ID, "shaped"), ShapedKubeJSRecipe.SerializerKJS::new);
		SHAPELESS = KubeJSRegistries.recipeSerializers().register(new ResourceLocation(KubeJS.MOD_ID, "shapeless"), ShapelessKubeJSRecipe.SerializerKJS::new);
	}
}
