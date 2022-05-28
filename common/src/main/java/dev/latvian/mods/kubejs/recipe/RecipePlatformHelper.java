package dev.latvian.mods.kubejs.recipe;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface RecipePlatformHelper {

	Supplier<RecipePlatformHelper> INSTANCE = Suppliers.memoize(() -> {
		var serviceLoader = ServiceLoader.load(RecipePlatformHelper.class);
		return serviceLoader.findFirst().orElseThrow(() -> new RuntimeException("Could not find platform implementation for RecipePlatformHelper!"));
	});

	static RecipePlatformHelper get() {
		return INSTANCE.get();
	}

	Recipe<?> fromJson(RecipeJS self) throws Throwable;

	Ingredient getCustomIngredient(JsonObject object);

	void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map);

	boolean processConditions(RecipeManager recipeManager, JsonObject json, String key);

	Object createRecipeContext(ReloadableServerResources resources);
}
