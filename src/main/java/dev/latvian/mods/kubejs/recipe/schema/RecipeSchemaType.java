package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class RecipeSchemaType {
	public final RecipeNamespace namespace;
	public final Identifier id;
	public final RecipeSchema schema;
	public final ResourceKey<RecipeSerializer<?>> serializerKey;
	public final String serializerType;
	public RecipeSchemaType parent;
	protected Optional<RecipeSerializer<?>> serializer;

	public RecipeSchemaType(RecipeNamespace namespace, Identifier id, RecipeSchema schema) {
		this.namespace = namespace;
		this.id = id;
		this.schema = schema;
		this.serializerKey = ResourceKey.create(Registries.RECIPE_SERIALIZER, schema.typeOverride == null ? id : schema.typeOverride);
		serializerType = serializerKey.identifier().toString();
	}

	public RecipeSerializer<?> getSerializer() {
		if (serializer.isEmpty()) {
			serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(serializerKey).map(Holder.Reference::value);
		}

		var s = serializer.orElse(null);

		if (s == null) {
			throw new KubeRuntimeException("Serializer for type " + serializerKey.identifier() + " is not found!");
		}

		return s;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
