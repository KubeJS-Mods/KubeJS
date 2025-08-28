package dev.latvian.mods.kubejs.recipe.schema.postprocessing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.TinyMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record KeyPatternCleanupPostProcessor(String patternName, String keyName, RecipeComponent<?> component) implements RecipePostProcessor {
	public static final RecipePostProcessorType<KeyPatternCleanupPostProcessor> TYPE = new RecipePostProcessorType<>(KubeJS.id("key_pattern_cleanup"), ctx -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.optionalFieldOf("pattern", "pattern").forGetter(KeyPatternCleanupPostProcessor::patternName),
		Codec.STRING.optionalFieldOf("key", "key").forGetter(KeyPatternCleanupPostProcessor::keyName),
		ctx.recipeComponentCodec().optionalFieldOf("component", IngredientComponent.INGREDIENT.instance()).forGetter(KeyPatternCleanupPostProcessor::component)
	).apply(instance, KeyPatternCleanupPostProcessor::new)));

	@Override
	public RecipePostProcessorType<?> type() {
		return TYPE;
	}

	@Override
	public void process(RecipeValidationContext ctx, KubeRecipe recipe) {
		RecipeKey<TinyMap<Character, ?>> keyKey = recipe.type.schemaType.schema.getKey(keyName);
		var key = recipe.getValue(keyKey);

		CharList airs = null;

		var entries = new ArrayList<>(Arrays.asList(key.entries()));
		var itr = entries.iterator();

		while (itr.hasNext()) {
			var entry = itr.next();
			if (entry.value() == null || component.isEmpty(Cast.to(entry.value()))) {
				if (airs == null) {
					airs = new CharArrayList(1);
				}

				airs.add(entry.key().charValue());
				itr.remove();
			}
		}

		if (airs != null) {
			RecipeKey<List<String>> patternKey = recipe.type.schemaType.schema.getKey(patternName);
			var pattern = new ArrayList<>(recipe.getValue(patternKey));

			var airChars = airs.toCharArray();

			for (int i = 0; i < pattern.size(); i++) {
				for (var a : airChars) {
					pattern.set(i, pattern.get(i).replace(a, ' '));
				}
			}

			recipe.setValue(patternKey, pattern);
			recipe.setValue(keyKey, new TinyMap(entries));
		}
	}
}
