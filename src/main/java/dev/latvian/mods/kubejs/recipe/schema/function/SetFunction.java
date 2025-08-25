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
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record SetFunction(String key, JsonElement value) implements RecipeSchemaFunction {
	public static final RecipeSchemaFunctionType<SetFunction> TYPE = new RecipeSchemaFunctionType<>("set", RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("key").forGetter(SetFunction::key),
		ExtraCodecs.JSON.fieldOf("value").forGetter(SetFunction::value)
	).apply(instance, SetFunction::new)));

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

		var v = k.codec.parse(jsonOps, value);

		if (v.isSuccess()) {
			return DataResult.success(new Resolved<>(k, v.getOrThrow()));
		} else {
			return v.ap(DataResult.error(() -> "Failed to parse value for key '" + key));
		}
	}

	public record Resolved<T>(RecipeKey<T> key, T to) implements ResolvedRecipeSchemaFunction {
		@Override
		public void execute(Context cx, KubeRecipe recipe, List<Object> args) {
			recipe.setValue(key, to);
		}
	}
}