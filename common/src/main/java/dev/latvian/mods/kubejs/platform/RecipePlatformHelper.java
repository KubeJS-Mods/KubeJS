package dev.latvian.mods.kubejs.platform;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public interface RecipePlatformHelper {

	Lazy<RecipePlatformHelper> INSTANCE = Lazy.serviceLoader(RecipePlatformHelper.class);

	static RecipePlatformHelper get() {
		return INSTANCE.get();
	}

	Recipe<?> fromJson(RecipeJS self) throws Throwable;

	Ingredient getCustomIngredient(JsonObject object);

	void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map);

	boolean processConditions(RecipeManager recipeManager, JsonObject json, String key);

	Object createRecipeContext(ReloadableServerResources resources);
}
