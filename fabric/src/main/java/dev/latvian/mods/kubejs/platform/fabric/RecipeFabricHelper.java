package dev.latvian.mods.kubejs.platform.fabric;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class RecipeFabricHelper implements RecipePlatformHelper {
	@Override
	@Nullable
	public RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		return new RecipeHolder<>(id, UtilsJS.fromJsonOrThrow(json, serializer.codec()));
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
	public void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> map) {
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class)
			.forEach(consumer -> consumer.accept(map));
	}

}
