package dev.latvian.mods.kubejs.recipe.schema.function;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record CustomFunction(ResourceLocation id) implements RecipeSchemaFunction {
	public static final RecipeSchemaFunctionType<CustomFunction> TYPE = new RecipeSchemaFunctionType<>("custom", RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(CustomFunction::id)
	).apply(instance, CustomFunction::new)));

	public static final Lazy<Map<ResourceLocation, ResolvedRecipeSchemaFunction>> MAP = Lazy.map(map -> KubeJSPlugins.forEachPlugin(p -> p.registerCustomRecipeSchemaFunctions(map::put)));

	@Override
	public RecipeSchemaFunctionType<?> type() {
		return TYPE;
	}

	@Override
	public DataResult<ResolvedRecipeSchemaFunction> resolve(DynamicOps<JsonElement> jsonOps, RecipeSchema schema) {
		var func = MAP.get().get(id);

		if (func == null) {
			return DataResult.error(() -> "Function '" + id + "' not registered");
		}

		return DataResult.success(func);
	}
}