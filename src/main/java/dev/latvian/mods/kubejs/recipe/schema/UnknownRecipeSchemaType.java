package dev.latvian.mods.kubejs.recipe.schema;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class UnknownRecipeSchemaType extends RecipeSchemaType {
	public UnknownRecipeSchemaType(RecipeNamespace namespace, Identifier id, RecipeSerializer<?> serializer) {
		super(namespace, id, UnknownRecipeSchema.SCHEMA);
		this.serializer = Optional.ofNullable(serializer);
	}
}
