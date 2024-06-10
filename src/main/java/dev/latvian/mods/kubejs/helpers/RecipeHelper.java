package dev.latvian.mods.kubejs.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.Nullable;

public enum RecipeHelper {
	INSTANCE;

	public static RecipeHelper get() {
		return INSTANCE;
	}

	@Nullable
	public RecipeHolder<?> fromJson(RegistryOps<JsonElement> ops, RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		try {
			return new RecipeHolder<>(id, serializer.codec().decode(ops, ops.getMap(json).result().get()).getOrThrow());
		} catch (Exception e) {
			if (!FMLLoader.isProduction()) {
				ConsoleJS.SERVER.error("Error parsing recipe " + id, e, RecipesKubeEvent.CREATE_RECIPE_SKIP_ERROR);
			}

			return null;
		}
	}

	public DataResult<JsonObject> validate(RegistryOps<JsonElement> ops, JsonElement jsonElement) {
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
