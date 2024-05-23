package dev.latvian.mods.kubejs.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.server.KubeJSReloadListener;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
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

	@Nullable
	public JsonObject checkConditions(JsonObject json) {
		var context = KubeJSReloadListener.resources.getConditionContext();
		var registry = KubeJSReloadListener.resources.getRegistryAccess();
		var ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, registry), context);

		if (!json.has("type")) {
			return null;
		} else if (json.get(ConditionalOps.DEFAULT_CONDITIONS_KEY).isJsonArray() && !ICondition.conditionsMatched(ops, json)) {
			return null;
		}

		return json;
	}

	public Ingredient getCustomIngredient(JsonObject object) {
		return Ingredient.fromJson(object, false);
	}
}