package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;

import java.util.ArrayList;
import java.util.List;

public record BundleFunction(List<RecipeSchemaFunction> functions) implements RecipeSchemaFunction {
	public static final RecipeSchemaFunctionType<BundleFunction> TYPE = new RecipeSchemaFunctionType<>("bundle", RecordCodecBuilder.mapCodec(instance -> instance.group(
		RecipeSchemaFunction.CODEC.listOf().fieldOf("functions").forGetter(BundleFunction::functions)
	).apply(instance, BundleFunction::new)));

	@Override
	public RecipeSchemaFunctionType<?> type() {
		return TYPE;
	}

	@Override
	public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
		var list = new ArrayList<ResolvedRecipeSchemaFunction>(functions.size());

		for (int i = 0; i < functions.size(); i++) {
			var function = functions.get(i);
			var r = function.resolve(jsonOps, schema);

			if (r.isError()) {
				int j = i + 1;
				return r.ap(DataResult.error(() -> "Failed to parse function #" + j));
			}

			list.add(r.getOrThrow());
		}

		if (list.isEmpty()) {
			return DataResult.error(() -> "Bundled function list is empty");
		}

		return DataResult.success(new Resolved(list));
	}

	public record Resolved(List<ResolvedRecipeSchemaFunction> functions) implements ResolvedRecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, List<Object> args) {
			for (var function : functions) {
				function.execute(cx, recipe, args);
			}
		}
	}
}