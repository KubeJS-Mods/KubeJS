package dev.latvian.mods.kubejs.recipe.forge;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ConditionContext;

import java.util.Map;

public class RecipeEventJSImpl {
	public static boolean processConditions(RecipeManager recipeManager, JsonObject json, String key) {
		// extract context from RecipeManager
		// Lnet/minecraft/world/item/crafting/RecipeManager;context:Lnet/minecraftforge/common/crafting/conditions/ICondition$IContext;
		return !json.has(key) || CraftingHelper.processConditions(json, key, new ConditionContext(KubeJSReloadListener.resources.tagManager));
	}


	public static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
	}
}
