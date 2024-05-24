package dev.latvian.mods.kubejs.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.jetbrains.annotations.Nullable;

public enum RecipeHelper {
	INSTANCE;

	public static RecipeHelper get() {
		return INSTANCE;
	}

	@Nullable
	public RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		return RecipeManager.fromJson(id, json, JsonOps.INSTANCE).orElse(null);
	}

	public DataResult<JsonObject> validate(JsonElement jsonElement) {
		if (!jsonElement.isJsonObject()) {
			return DataResult.error(() -> "not a json object: " + jsonElement);
		}

		var json = GsonHelper.convertToJsonObject(jsonElement, "top element");
		var context = KubeJSReloadListener.resources.getConditionContext();
		var registry = KubeJSReloadListener.resources.getRegistryAccess();

		if (!json.has("type")) {
			return DataResult.error(() -> "missing type");
		}

		var ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, registry), context);
		var codec = ConditionalOps.createConditionalCodec(Codec.unit(json));

		return codec.parse(ops, json)
			.mapError(str -> "error while parsing conditions: " + str)
			.flatMap(optional -> optional
				.map(DataResult::success)
				.orElseGet(() -> DataResult.error(() -> "conditions not met")));
	}

	public Ingredient getCustomIngredient(JsonObject object) {
		return Ingredient.fromJson(object, false);
	}
}
