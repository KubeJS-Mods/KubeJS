package dev.latvian.mods.kubejs.platform.forge;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.mixin.forge.RecipeManagerAccessor;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RecipePlatformHelperImpl implements RecipePlatformHelper {
	public static final String FORGE_CONDITIONAL = "forge:conditional";

	@Override
	@Nullable
	public Recipe<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		return serializer.fromJson(id, json, (ICondition.IContext) KubeJSReloadListener.recipeContext);
	}

	@Override
	@Nullable
	public JsonObject checkConditions(JsonObject json) {
		if (!json.has("type")) {
			return null;
		} else if (json.get("type").getAsString().equals(FORGE_CONDITIONAL)) {
			var context = (ICondition.IContext) KubeJSReloadListener.recipeContext;

			for (var ele : GsonHelper.getAsJsonArray(json, "recipes")) {
				if (!ele.isJsonObject()) {
					return null;
				} else if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context)) {
					return GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "recipe");
				}
			}

			return null;
		}

		return json;
	}

	@Override
	public Ingredient getCustomIngredient(JsonObject object) {
		return CraftingHelper.getIngredient(object);
	}

	@Override
	public boolean processConditions(RecipeManager recipeManager, JsonObject json) {
		return !json.has("conditions") || CraftingHelper.processConditions(json, "conditions", (ICondition.IContext) KubeJSReloadListener.recipeContext);
	}

	@Override
	public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
	}

	@Override
	public Object createRecipeContext(ReloadableServerResources resources) {
		return ((RecipeManagerAccessor) resources.getRecipeManager()).getContext();
	}

	@Override
	@Nullable
	public Player getCraftingPlayer() {
		return ForgeHooks.getCraftingPlayer();
	}
}
