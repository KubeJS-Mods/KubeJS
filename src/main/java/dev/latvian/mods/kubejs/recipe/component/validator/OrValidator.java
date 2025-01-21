package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.List;

public record OrValidator(List<RecipeComponentValidator> list) implements RecipeComponentValidator {
	public static final RecipeComponentValidatorType<OrValidator> TYPE = new RecipeComponentValidatorType<>(KubeJS.id("or"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		RecipeComponentValidator.CODEC.listOf().fieldOf("list").forGetter(OrValidator::list)
	).apply(instance, OrValidator::new)));

	@Override
	public RecipeComponentValidatorType<?> getType() {
		return TYPE;
	}

	@Override
	public <T> boolean isValid(RecipeComponent<T> component, T value) {
		for (var validator : list) {
			if (validator.isValid(component, value)) {
				return true;
			}
		}

		return false;
	}
}
