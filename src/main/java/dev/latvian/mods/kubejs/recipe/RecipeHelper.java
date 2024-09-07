package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public interface RecipeHelper {
	@Nullable
	static RecipeHolder<?> fromJson(DynamicOps<JsonElement> ops, RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json, boolean errors) {
		var codec = serializer.codec();

		if (codec == null) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Codec not found in " + serializer.getClass().getName());
			} else {
				RecipeManager.LOGGER.error("Error parsing recipe " + id + ": Codec not found in " + serializer.getClass().getName());
			}

			return null;
		}

		var map = ops.getMap(json).result();

		if (map.isEmpty()) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Couldn't convert " + json + " to a map");
			} else {
				RecipeManager.LOGGER.error("Error parsing recipe " + id + ": Couldn't convert " + json + " to a map");
			}

			return null;
		}

		try {
			var recipe = codec.decode(ops, map.get());

			if (recipe.isSuccess()) {
				var r = recipe.getOrThrow();
				return r == null ? null : new RecipeHolder<>(id, r);
			} else if (recipe.error().isPresent()) {
				if (errors) {
					ConsoleJS.SERVER.error("Error parsing recipe " + id + ": " + recipe.error().get().message());
				} else {
					RecipeManager.LOGGER.error("Error parsing recipe " + id + ": " + recipe.error().get().message());
				}
			} else {
				if (errors) {
					ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Unknown");
				} else {
					RecipeManager.LOGGER.error("Error parsing recipe " + id + ": Unknown");
				}
			}
		} catch (Exception e) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + " from " + map.get(), e, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
			} else {
				RecipeManager.LOGGER.error("Error parsing recipe " + id + " from " + map.get(), e);
			}
		}

		return null;
	}
}
