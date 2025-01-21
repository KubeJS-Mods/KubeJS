package dev.latvian.mods.kubejs.recipe.component.validator;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

import java.util.List;

public record AndValidator(List<RecipeComponentValidator> list) implements RecipeComponentValidator {
	public static final RecipeComponentValidatorType<AndValidator> TYPE = new RecipeComponentValidatorType<>(KubeJS.id("and"), RecordCodecBuilder.mapCodec(instance -> instance.group(
		RecipeComponentValidator.CODEC.listOf().fieldOf("list").forGetter(AndValidator::list)
	).apply(instance, AndValidator::new)));

	@Override
	public RecipeComponentValidatorType<?> getType() {
		return TYPE;
	}

	@Override
	public <T> boolean isValid(RecipeComponent<T> component, T value) {
		for (var validator : list) {
			if (!validator.isValid(component, value)) {
				return false;
			}
		}

		return true;
	}
}
