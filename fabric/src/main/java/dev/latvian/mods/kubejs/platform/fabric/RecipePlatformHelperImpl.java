package dev.latvian.mods.kubejs.platform.fabric;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class RecipePlatformHelperImpl implements RecipePlatformHelper {
	@Override
	@Nullable
	public Recipe<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		return serializer.fromJson(id, json);
	}

	@Override
	public JsonObject checkConditions(JsonObject json) {
		return json;
	}

	@Override
	public Ingredient getCustomIngredient(JsonObject object) {
		return IngredientJS.ofJson(object);
	}

	@Override
	public boolean processConditions(RecipeManager recipeManager, JsonObject json) {
		return true;
	}

	@Override
	public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map) {
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class).forEach(consumer -> consumer.accept(map));
	}

	@Override
	public Object createRecipeContext(ReloadableServerResources resources) {
		return null;
	}

	@Override
	@Nullable
	public Player getCraftingPlayer() {
		// TODO: Implement this on the Fabric side
		return null;
	}
}
