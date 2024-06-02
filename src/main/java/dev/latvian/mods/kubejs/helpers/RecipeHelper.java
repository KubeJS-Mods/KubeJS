package dev.latvian.mods.kubejs.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
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
	public RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		try {
			return new RecipeHolder<>(id,
				serializer.codec().decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).result().get()).getOrThrow());
		} catch (Exception e) {
			if (!FMLLoader.isProduction()) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public DataResult<JsonObject> validate(HolderLookup.Provider registry, JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			return DataResult.error(() -> "not a json object: " + jsonElement);
		}

		var json = GsonHelper.convertToJsonObject(jsonElement, "top element");

		if (!json.has("type")) {
			return DataResult.error(() -> "missing type");
		}

		var ops = ConditionalOps.create(JsonOps.INSTANCE, registry);
		var codec = ConditionalOps.createConditionalCodec(Codec.unit(json));

		return codec.parse(ops, json)
			.mapError(str -> "error while parsing conditions: " + str)
			.flatMap(optional -> optional
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "conditions not met")));
	}

	public Ingredient getCustomIngredient(JsonObject object) {
		return Ingredient.CODEC.decode(JsonOps.INSTANCE, object).getOrThrow().getFirst();
	}
}
