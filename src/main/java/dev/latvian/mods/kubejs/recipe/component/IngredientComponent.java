package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IngredientComponent implements RecipeComponent<Ingredient> {
	public static final IngredientComponent INGREDIENT = new IngredientComponent("ingredient", Ingredient.CODEC);
	public static final IngredientComponent NON_EMPTY_INGREDIENT = new IngredientComponent("ingredient", Ingredient.CODEC_NONEMPTY);

	public static final RecipeComponent<List<Ingredient>> UNWRAPPED_INGREDIENT_LIST = new RecipeComponentWithParent<>() {
		private static final TypeInfo WRAP_TYPE = TypeInfo.RAW_LIST.withParams(TypeInfo.of(SizedIngredient.class));

		@Override
		public RecipeComponent<List<Ingredient>> parentComponent() {
			return INGREDIENT.asList();
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

	public final String name;
	public final Codec<Ingredient> codec;

	public IngredientComponent(String name, Codec<Ingredient> codec) {
		this.name = name;
		this.codec = codec;
	}

	@Override
	public Codec<Ingredient> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return IngredientJS.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof InputItem || from instanceof ItemStack || from instanceof Ingredient || !InputItem.of(from).isEmpty();
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
	@Nullable
	public String createUniqueId(Ingredient value) {
		var item = value == null ? null : value.kjs$getFirst();
		return item == null || item.isEmpty() ? null : RecipeSchema.normalizeId(item.kjs$getId()).replace('/', '_');
	}

	@Override
	public String toString() {
		return name;
	}
}
