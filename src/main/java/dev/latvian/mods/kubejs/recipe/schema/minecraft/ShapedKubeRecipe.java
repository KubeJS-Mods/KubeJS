package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.ValidationContext;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.special.KubeJSCraftingRecipe;
import dev.latvian.mods.kubejs.util.TinyMap;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapedKubeRecipe extends KubeRecipe {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("shaped"), ShapedKubeRecipe.class, ShapedKubeRecipe::new);

	@Override
	public void validate(ValidationContext ctx) {
		RecipeKey<List<String>> patternKey = type.schemaType.schema.getKey("pattern");
		RecipeKey<TinyMap<Character, Ingredient>> keyKey = type.schemaType.schema.getKey("key");

		var pattern = new ArrayList<>(getValue(patternKey));
		var key = getValue(keyKey);

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

			setValue(patternKey, pattern);
			setValue(keyKey, new TinyMap<>(entries));
		}
	}

	@Override
	public RecipeTypeFunction getSerializationTypeFunction() {
		// Use vanilla shaped recipe type if KubeJS is not needed
		if (type == type.event.shaped // if this type == kubejs:shaped
			&& type.event.shaped != type.event.vanillaShaped // check if not in serverOnly mode
			&& !json.has(KubeJSCraftingRecipe.INGREDIENT_ACTIONS_KEY)
			&& !json.has(KubeJSCraftingRecipe.MODIFY_RESULT_KEY)
			&& !json.has(KubeJSCraftingRecipe.STAGE_KEY)
			&& !json.has(KubeJSCraftingRecipe.MIRROR_KEY)
		) {
			return type.event.vanillaShaped;
		}

		return super.getSerializationTypeFunction();
	}
}
