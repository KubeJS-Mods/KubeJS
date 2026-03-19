package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jspecify.annotations.Nullable;

public class RecipeSchemaType {
	public final RecipeNamespace namespace;
	public final Identifier id;
	public final RecipeSchema schema;
	public final ResourceKey<RecipeSerializer<?>> serializerKey;
	public final String serializerType;
	public @Nullable RecipeSchemaType parent;
	protected final Lazy<RecipeSerializer<?>> serializer;

	protected RecipeSchemaType(RecipeNamespace namespace, Identifier id, RecipeSchema schema) {
		this(namespace, id, schema, null);
	}

	protected RecipeSchemaType(RecipeNamespace namespace, Identifier id, RecipeSchema schema, @Nullable RecipeSerializer<?> serializer) {
		this.namespace = namespace;
		this.id = id;
		this.schema = schema;
		this.serializerKey = ResourceKey.create(Registries.RECIPE_SERIALIZER, schema.typeOverride == null ? id : schema.typeOverride);
		serializerType = serializerKey.identifier().toString();
		this.serializer = Lazy.of(serializer != null ? () -> serializer : this::serializerFromRegistry);
	}

	private RecipeSerializer<?> serializerFromRegistry() {
		return BuiltInRegistries.RECIPE_SERIALIZER.get(serializerKey)
			.map(Holder::value)
			.orElseThrow(() -> new KubeRuntimeException("Serializer for type %s is not found!".formatted(serializerKey.identifier())));
	}

	public RecipeSerializer<?> getSerializer() {
		return serializer.get();
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
