package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ShapedRecipeSchema {
	class ShapedKubeRecipe extends KubeRecipe {
		public KubeRecipe noMirror() {
			return setValue(KJS_MIRROR, false);
		}

		public KubeRecipe noShrink() {
			return setValue(KJS_SHRINK, false);
		}

		@Override
		public void afterLoaded() {
			super.afterLoaded();
			var pattern = new ArrayList<>(getValue(PATTERN));
			var key = getValue(KEY);

			if (pattern.isEmpty()) {
				throw new RecipeExceptionJS("Pattern is empty!");
			}

			if (key.isEmpty()) {
				throw new RecipeExceptionJS("Key map is empty!");
			}

			List<Character> airs = null;

			var entries = new ArrayList<>(Arrays.asList(key.entries()));
			var itr = entries.iterator();

			while (itr.hasNext()) {
				var entry = itr.next();
				if (entry.value() == null || entry.value().isEmpty()) {
					if (airs == null) {
						airs = new ArrayList<>(1);
					}

					airs.add(entry.key());
					itr.remove();
				}
			}

			if (airs != null) {
				for (int i = 0; i < pattern.size(); i++) {
					for (var a : airs) {
						pattern.set(i, pattern.get(i).replace(a, ' '));
					}
				}

				setValue(PATTERN, pattern);
				setValue(KEY, new TinyMap<>(entries));
			}
		}

		@Override
		public RecipeTypeFunction getSerializationTypeFunction() {
			// Use vanilla shaped recipe type if KubeJS is not needed
			if (type == type.event.shaped // if this type == kubejs:shaped
				&& type.event.shaped != type.event.vanillaShaped // check if not in serverOnly mode
				&& !json.has("kubejs:actions")
				&& !json.has("kubejs:modify_result")
				&& !json.has("kubejs:stage")
				&& !json.has("kubejs:mirror")
				&& !json.has("kubejs:shrink")
			) {
				return type.event.vanillaShaped;
			}

			return super.getSerializationTypeFunction();
		}
	}

	RecipeKey<ItemStack> RESULT = ItemComponents.OUTPUT.outputKey("result");
	RecipeKey<List<String>> PATTERN = StringComponent.NON_EMPTY.asList().otherKey("pattern");
	RecipeKey<TinyMap<Character, Ingredient>> KEY = MapRecipeComponent.INGREDIENT_PATTERN_KEY.inputKey("key");
	RecipeKey<Boolean> KJS_MIRROR = BooleanComponent.BOOLEAN.otherKey("kubejs:mirror").preferred("kjsMirror").optional(true).exclude();
	RecipeKey<Boolean> KJS_SHRINK = BooleanComponent.BOOLEAN.otherKey("kubejs:shrink").preferred("kjsShrink").optional(true).exclude();

	RecipeSchema SCHEMA = new RecipeSchema(ShapedKubeRecipe.class, ShapedKubeRecipe::new, RESULT, PATTERN, KEY, KJS_MIRROR, KJS_SHRINK)
		.constructor(RESULT, PATTERN, KEY)
		.uniqueOutputId(RESULT);
}
