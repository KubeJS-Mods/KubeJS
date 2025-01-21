package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public class AlwaysValidValidator implements RecipeComponentValidator {
	public static final AlwaysValidValidator INSTANCE = new AlwaysValidValidator();
	public static final RecipeComponentValidatorType<AlwaysValidValidator> TYPE = new RecipeComponentValidatorType<>(KubeJS.id("always_valid"), MapCodec.unit(INSTANCE));

	@Override
	public RecipeComponentValidatorType<?> getType() {
		return TYPE;
	}

	@Override
	public <T> boolean isValid(RecipeComponent<T> component, T value) {
		return true;
	}
}
