package dev.latvian.mods.kubejs.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public enum RecipeHelper {
	INSTANCE;

	public static RecipeHelper get() {
		return INSTANCE;
	}

	public static final String FORGE_CONDITIONAL = "neoforge:conditional";

	@Nullable
	public RecipeHolder<?> fromJson(RecipeSerializer<?> serializer, ResourceLocation id, JsonObject json) {
		try {
			return new RecipeHolder<>(id, serializer.codec().decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).result().get()).getOrThrow());
		} catch (Exception e) {
			if (!FMLLoader.isProduction()) {
				e.printStackTrace();
			}

			return null;
		}
	}

	@Nullable
	public JsonObject checkConditions(HolderLookup.Provider registry, JsonObject json) {
		var ops = ConditionalOps.create(JsonOps.INSTANCE, registry);

		if (!json.has("type")) {
			return null;
		} else if (json.get(ConditionalOps.DEFAULT_CONDITIONS_KEY) instanceof JsonArray arr && !ICondition.conditionsMatched(ops, arr)) {
			return null;
		}

		return json;
	}

	public Ingredient getCustomIngredient(JsonObject object) {
		return Ingredient.CODEC.decode(JsonOps.INSTANCE, object).getOrThrow().getFirst();
	}
}
