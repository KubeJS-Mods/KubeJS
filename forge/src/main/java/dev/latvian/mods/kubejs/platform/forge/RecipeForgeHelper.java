package dev.latvian.mods.kubejs.platform.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class RecipeForgeHelper implements RecipePlatformHelper {
	public static final String FORGE_CONDITIONAL = "forge:conditional";

	@Override
	@Nullable
	public RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		// TODO: What is this mess, Forge???
		return RecipeManager.fromJson(id, json, JsonOps.INSTANCE).orElse(null);
	}

	@Override
	@Nullable
	public JsonObject checkConditions(JsonObject json) {
		var context = KubeJSReloadListener.resources.getConditionContext();

		if (!json.has("type")) {
			return null;
		} else if (json.get("type").getAsString().equals(FORGE_CONDITIONAL)) {
			for (var ele : GsonHelper.getAsJsonArray(json, "recipes")) {
				if (!ele.isJsonObject()) {
					return null;
				} else if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context)) {
					return GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "recipe");
				}
			}

			return null;
		} else if (json.get("conditions") instanceof JsonArray arr && !CraftingHelper.processConditions(arr, context)) {
			return null;
		}

		return json;
	}

	@Override
	public Ingredient getCustomIngredient(JsonObject object) {
		return CraftingHelper.getIngredient(object, false);
	}

	@Override
	public boolean processConditions(RecipeManager recipeManager, JsonObject json) {
		return !json.has("conditions") || CraftingHelper.processConditions(json, "conditions", KubeJSReloadListener.resources.getConditionContext());
	}
}
