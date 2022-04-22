package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public class RecipePlatformHelper {

	@ExpectPlatform
	public static Recipe<?> fromJson(RecipeJS self) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@ExpectPlatform
	public static Ingredient getCustomIngredient(JsonObject object) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean processConditions(RecipeManager recipeManager, JsonObject json, String key) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Object createRecipeContext(ReloadableServerResources resources) {
		throw new AssertionError();
	}
}
