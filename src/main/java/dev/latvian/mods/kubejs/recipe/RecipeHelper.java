package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.Nullable;

public interface RecipeHelper {
	@Nullable
	static RecipeHolder<?> fromJson(DynamicOps<JsonElement> ops, RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json, boolean errors) {
		var codec = serializer.codec();

		if (codec == null) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Codec not found in " + serializer.getClass().getName());
			}

			return null;
		}

		var map = ops.getMap(json).result();

		if (map.isEmpty()) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Couldn't convert " + json + " to a map");
			}

			return null;
		}

		try {
			var recipe = codec.decode(ops, map.get());

			if (recipe.isSuccess()) {
				return new RecipeHolder<>(id, recipe.getOrThrow());
			} else if (recipe.error().isPresent()) {
				if (errors) {
					ConsoleJS.SERVER.error("Error parsing recipe " + id + ": " + recipe.error().get().message());
				}
			} else {
				if (errors) {
					ConsoleJS.SERVER.error("Error parsing recipe " + id + ": Unknown");
				}
			}
		} catch (Exception e) {
			if (errors) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id + " from " + map.get(), e, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
			}
		}

		return null;
	}

	static DataResult<JsonObject> validate(DynamicOps<JsonElement> ops, JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			return DataResult.error(() -> "not a json object: " + jsonElement);
		}

		var json = GsonHelper.convertToJsonObject(jsonElement, "top element");

		if (!json.has("type")) {
			return DataResult.error(() -> "missing type");
		}

		var codec = ConditionalOps.createConditionalCodec(Codec.unit(json));

		return codec.parse(ops, json)
			.mapError(str -> "error while parsing conditions: " + str)
			.flatMap(optional -> optional
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "conditions not met")));
	}
}
