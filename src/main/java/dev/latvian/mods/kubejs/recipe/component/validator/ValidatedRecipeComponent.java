package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.EmptyRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentCodecFactory;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;

public record ValidatedRecipeComponent<T>(RecipeComponent<T> component, RecipeComponentValidator validator) implements RecipeComponentWithParent<T> {
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(KubeJS.id("validated"), (RecipeComponentCodecFactory<ValidatedRecipeComponent<?>>) ctx -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.codec().fieldOf("component").forGetter(ValidatedRecipeComponent::component),
		RecipeComponentValidator.CODEC.fieldOf("validator").forGetter(ValidatedRecipeComponent::validator)
	).apply(instance, ValidatedRecipeComponent::new)));

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	public RecipeComponent<T> parentComponent() {
		return component;
	}

	@Override
	public void validate(T value) {
		if (!validator.isValid(this, value)) {
			throw new EmptyRecipeComponentValueException(this);
		}
	}
}
