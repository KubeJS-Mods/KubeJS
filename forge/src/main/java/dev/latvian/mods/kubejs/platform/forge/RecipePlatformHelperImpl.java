package dev.latvian.mods.kubejs.platform.forge;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.mixin.forge.RecipeManagerAccessor;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.Map;

public class RecipePlatformHelperImpl implements RecipePlatformHelper {

	@Override
	public Recipe<?> fromJson(RecipeJS self) throws Throwable {
		return self.type.serializer.fromJson(self.getOrCreateId(), self.json, (ICondition.IContext) KubeJSReloadListener.recipeContext);
	}

	@Override
	public Ingredient getCustomIngredient(JsonObject object) {
		return CraftingHelper.getIngredient(object);
	}

	@Override
	public boolean processConditions(RecipeManager recipeManager, JsonObject json, String key) {
		return !json.has(key) || CraftingHelper.processConditions(json, key, (ICondition.IContext) KubeJSReloadListener.recipeContext);
	}

	@Override
	public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
	}

	@Override
	public Object createRecipeContext(ReloadableServerResources resources) {
		return ((RecipeManagerAccessor) resources.getRecipeManager()).getContext();
	}
}
