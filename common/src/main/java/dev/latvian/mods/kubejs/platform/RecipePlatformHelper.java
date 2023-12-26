package dev.latvian.mods.kubejs.platform;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RecipePlatformHelper {
	Lazy<RecipePlatformHelper> INSTANCE = Lazy.serviceLoader(RecipePlatformHelper.class);

	static RecipePlatformHelper get() {
		return INSTANCE.get();
	}

	@Nullable
	RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json);

	@Nullable
	JsonObject checkConditions(JsonObject json);

	Ingredient getCustomIngredient(JsonObject object);

	default void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> map) {
		// Fabric only
	}
}
