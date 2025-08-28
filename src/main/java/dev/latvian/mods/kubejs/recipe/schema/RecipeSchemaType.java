package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class RecipeSchemaType {
	public final RecipeNamespace namespace;
	public final ResourceLocation id;
	public final RecipeSchema schema;
	public final ResourceKey<RecipeSerializer<?>> serializerKey;
	public RecipeSchemaType parent;
	protected Optional<RecipeSerializer<?>> serializer;

	public RecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSchema schema) {
		this.namespace = namespace;
		this.id = id;
		this.schema = schema;
		this.serializerKey = ResourceKey.create(Registries.RECIPE_SERIALIZER, schema.typeOverride == null ? id : schema.typeOverride);
	}

	public RecipeSerializer<?> getSerializer() {
		if (serializer == null) {
			serializer = Optional.ofNullable(BuiltInRegistries.RECIPE_SERIALIZER.get(serializerKey));
		}

		var s = serializer.orElse(null);

		if (s == null) {
			throw new KubeRuntimeException("Serializer for type " + serializerKey.location() + " is not found!");
		}

		return s;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
