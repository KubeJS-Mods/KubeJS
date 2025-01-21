package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class NonEmptyValidator implements RecipeComponentValidator {
	public static final NonEmptyValidator INSTANCE = new NonEmptyValidator();
	public static final RecipeComponentValidatorType<NonEmptyValidator> TYPE = new RecipeComponentValidatorType<>(KubeJS.id("non_empty"), MapCodec.unit(INSTANCE));

	@Override
	public RecipeComponentValidatorType<?> getType() {
		return TYPE;
	}

	@Override
	public <T> boolean isValid(RecipeComponent<T> component, T value) {
		return value != null && !component.isEmpty(value);
	}
}
