package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class RecipeSchemaType {
	public final RecipeNamespace namespace;
	public final ResourceLocation id;
	public final RecipeSchema schema;
	public RecipeSchemaType parent;
	protected Optional<RecipeSerializer<?>> serializer;

	public RecipeSchemaType(RecipeNamespace namespace, ResourceLocation id, RecipeSchema schema) {
		this.namespace = namespace;
		this.id = id;
		this.schema = schema;
	}

	public RecipeSerializer<?> getSerializer() {
		if (serializer == null) {
			serializer = Optional.ofNullable(KubeJSRegistries.recipeSerializers().get(id));
		}

		var s = serializer.orElse(null);

		if (s == null) {
			throw new RecipeExceptionJS("Serializer for type " + id + " is not found!");
		}

		return s;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
