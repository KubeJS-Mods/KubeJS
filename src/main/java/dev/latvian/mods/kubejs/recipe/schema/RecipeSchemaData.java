package dev.latvian.mods.kubejs.recipe.schema;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeSchemaFunction;
import dev.latvian.mods.kubejs.recipe.schema.postprocessing.RecipePostProcessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record RecipeSchemaData(
	Optional<ResourceLocation> parent,
	Optional<ResourceLocation> overrideType,
	Optional<ResourceLocation> recipeFactory,
	Optional<List<RecipeKeyData>> keys,
	Optional<List<ConstructorData>> constructors,
	Optional<Map<String, RecipeSchemaFunction>> functions,
	Map<String, JsonElement> overrideKeys,
	Optional<Boolean> hidden,
	List<String> mappings,
	Optional<List<String>> unique,
	Optional<List<RecipePostProcessor>> postProcessors,
	MergeData merge
) {
	public static Function<RecipeTypeRegistryContext, Codec<RecipeSchemaData>> CODEC = ctx -> RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(RecipeSchemaData::parent),
		ResourceLocation.CODEC.optionalFieldOf("override_type").forGetter(RecipeSchemaData::overrideType),
		ResourceLocation.CODEC.optionalFieldOf("recipe_factory").forGetter(RecipeSchemaData::recipeFactory),
		RecipeKeyData.CODEC.apply(ctx).listOf().optionalFieldOf("keys").forGetter(RecipeSchemaData::keys),
		ConstructorData.CODEC.listOf().optionalFieldOf("constructors").forGetter(RecipeSchemaData::constructors),
		Codec.unboundedMap(Codec.STRING, RecipeSchemaFunction.CODEC).optionalFieldOf("functions").forGetter(RecipeSchemaData::functions),
		Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).optionalFieldOf("override_keys", Map.of()).forGetter(RecipeSchemaData::overrideKeys),
		Codec.BOOL.optionalFieldOf("hidden").forGetter(RecipeSchemaData::hidden),
		Codec.STRING.listOf().optionalFieldOf("mappings", List.of()).forGetter(RecipeSchemaData::mappings),
		Codec.STRING.listOf().optionalFieldOf("unique").forGetter(RecipeSchemaData::unique),
		ctx.recipePostProcessorCodec().listOf().optionalFieldOf("post_processors").forGetter(RecipeSchemaData::postProcessors),
		MergeData.CODEC.optionalFieldOf("merge", MergeData.DEFAULT).forGetter(RecipeSchemaData::merge)
	).apply(instance, RecipeSchemaData::new));

	public record RecipeKeyData(
		String name,
		ComponentRole role,
		RecipeComponent<?> type,
		Optional<JsonElement> optional,
		boolean defaultOptional,
		List<String> alternativeNames,
		boolean excluded,
		List<String> functionNames,
		boolean alwaysWrite
	) {
		public static Function<RecipeTypeRegistryContext, Codec<RecipeKeyData>> CODEC = ctx -> RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(RecipeKeyData::name),
			ComponentRole.CODEC.optionalFieldOf("role", ComponentRole.OTHER).forGetter(RecipeKeyData::role),
			ctx.recipeComponentCodec().fieldOf("type").forGetter(RecipeKeyData::type),
			ExtraCodecs.JSON.optionalFieldOf("optional").forGetter(RecipeKeyData::optional),
			Codec.BOOL.optionalFieldOf("default_optional", false).forGetter(RecipeKeyData::defaultOptional),
			Codec.STRING.listOf().optionalFieldOf("alternative_names", List.of()).forGetter(RecipeKeyData::alternativeNames),
			Codec.BOOL.optionalFieldOf("excluded", false).forGetter(RecipeKeyData::excluded),
			Codec.STRING.listOf().optionalFieldOf("function_names", List.of()).forGetter(RecipeKeyData::functionNames),
			Codec.BOOL.optionalFieldOf("always_write", false).forGetter(RecipeKeyData::alwaysWrite)
		).apply(instance, RecipeKeyData::new));
	}

	public record ConstructorData(
		List<String> keys,
		Map<String, JsonElement> overrides
	) {
		public static Codec<ConstructorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("keys").forGetter(ConstructorData::keys),
			Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).optionalFieldOf("overrides", Map.of()).forGetter(ConstructorData::overrides)
		).apply(instance, ConstructorData::new));
	}

	public record MergeData(
		boolean keys,
		boolean constructors,
		boolean unique,
		boolean postProcessors
	) {
		public static final MergeData DEFAULT = new MergeData(false, false, false, false);

		public static Codec<MergeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("keys", false).forGetter(MergeData::keys),
			Codec.BOOL.optionalFieldOf("constructors", false).forGetter(MergeData::constructors),
			Codec.BOOL.optionalFieldOf("unique", false).forGetter(MergeData::unique),
			Codec.BOOL.optionalFieldOf("post_processors", false).forGetter(MergeData::postProcessors)
		).apply(instance, MergeData::new));
	}
}
