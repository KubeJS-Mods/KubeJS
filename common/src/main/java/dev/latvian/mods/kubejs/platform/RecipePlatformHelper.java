package dev.latvian.mods.kubejs.platform;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RecipePlatformHelper {
	Lazy<RecipePlatformHelper> INSTANCE = Lazy.serviceLoader(RecipePlatformHelper.class);

	static RecipePlatformHelper get() {
		return INSTANCE.get();
	}

	@Nullable
	Recipe<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json);

	@Nullable
	JsonObject checkConditions(JsonObject json);

	Ingredient getCustomIngredient(JsonObject object);

	void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map);

	boolean processConditions(RecipeManager recipeManager, JsonObject json);

	Object createRecipeContext(ReloadableServerResources resources);
}
