package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public interface ItemComponents {
	RecipeComponent<Ingredient> INPUT = new RecipeComponent<>() {
		@Override
		public Codec<Ingredient> codec() {
			return Ingredient.CODEC;
		}

		@Override
		public TypeInfo typeInfo() {
			return IngredientJS.TYPE_INFO;
		}

		@Override
		public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
			return recipe.inputItemHasPriority(from);
		}

		@Override
		public boolean isInput(KubeRecipe recipe, Ingredient value, ReplacementMatch match) {
			return match instanceof ItemMatch m && m.contains(value);
		}

		@Override
		public String checkEmpty(RecipeKey<Ingredient> key, Ingredient value) {
			if (value.isEmpty()) {
				return "Ingredient '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public RecipeComponent<TinyMap<Character, Ingredient>> asPatternKey() {
			return MapRecipeComponent.INGREDIENT_PATTERN_KEY;
		}

		@Override
		public String toString() {
			return "ingredient";
		}
	};

	RecipeComponent<List<Ingredient>> INPUT_LIST = INPUT.asList();

	RecipeComponent<List<Ingredient>> UNWRAPPED_INPUT_LIST = new RecipeComponentWithParent<>() {
		private static final TypeInfo WRAP_TYPE = TypeInfo.RAW_LIST.withParams(TypeInfo.of(SizedIngredient.class));

		@Override
		public RecipeComponent<List<Ingredient>> parentComponent() {
			return ItemComponents.INPUT_LIST;
		}

		@Override
		public List<Ingredient> wrap(Context cx, KubeRecipe recipe, Object from) {
			var list = new ArrayList<Ingredient>();

			for (var in : (Iterable<SizedIngredient>) cx.jsToJava(from, WRAP_TYPE)) {
				for (int i = 0; i < in.count(); i++) {
					list.add(in.ingredient());
				}
			}

			return list;
		}

		@Override
		public String toString() {
			return "unwrapped_ingredient_list";
		}
	};

	RecipeComponent<ItemStack> OUTPUT = new RecipeComponent<>() {
		@Override
		public Codec<ItemStack> codec() {
			return ItemStack.OPTIONAL_CODEC;
		}

		@Override
		public TypeInfo typeInfo() {
			return ItemStackJS.TYPE_INFO;
		}

		@Override
		public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
			return recipe.outputItemHasPriority(from);
		}

		@Override
		public boolean isOutput(KubeRecipe recipe, ItemStack value, ReplacementMatch match) {
			return match instanceof ItemMatch m && !value.isEmpty() && m.contains(value);
		}

		@Override
		public String checkEmpty(RecipeKey<ItemStack> key, ItemStack value) {
			if (value.isEmpty()) {
				return "ItemStack '" + key.name + "' can't be empty!";
			}

			return "";
		}

		@Override
		public String toString() {
			return "output_item";
		}
	};

	RecipeComponent<List<ItemStack>> OUTPUT_LIST = OUTPUT.asList();
}
