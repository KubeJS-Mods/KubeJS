package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;

import java.util.ArrayList;
import java.util.List;

public record AddToListFunction(String key) implements RecipeSchemaFunction {
	public static final RecipeSchemaFunctionType<AddToListFunction> TYPE = new RecipeSchemaFunctionType<>("add_to_list", RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("key").forGetter(AddToListFunction::key)
	).apply(instance, AddToListFunction::new)));

	@Override
	public RecipeSchemaFunctionType<?> type() {
		return TYPE;
	}

	@Override
	public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
		var k = schema.getOptionalKey(key);

		if (k == null) {
			return DataResult.error(() -> "Key '" + key + "' not found");
		}

		return DataResult.success(new Resolved(k));
	}

	public record Resolved<T>(RecipeKey<List<T>> key) implements ResolvedRecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, Object[] args) {
			var value = recipe.getValue(key);
			var list = value == null ? new ArrayList<T>() : new ArrayList<>(value);
			list.addAll(key.component.wrap(cx, recipe, args));
			recipe.setValue(key, list);
		}
	}
}