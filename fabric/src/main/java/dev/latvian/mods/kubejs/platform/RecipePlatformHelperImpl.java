package dev.latvian.mods.kubejs.platform;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipePlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;
import java.util.function.Consumer;

public class RecipePlatformHelperImpl implements RecipePlatformHelper {
	public Recipe<?> fromJson(RecipeJS self) throws Throwable {
		return self.type.serializer.fromJson(self.getOrCreateId(), self.json);
	}

	public Ingredient getCustomIngredient(JsonObject object) {
		throw new UnsupportedOperationException("Custom ingredients are not present on Fabric!");
	}

	public boolean processConditions(RecipeManager recipeManager, JsonObject json, String key) {
		return true;
	}

	public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class).forEach(consumer -> consumer.accept(map));
	}

	public Object createRecipeContext(ReloadableServerResources resources) {
		return null;
	}
}
