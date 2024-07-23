package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientComponent implements RecipeComponent<Ingredient> {
	public static final IngredientComponent INGREDIENT = new IngredientComponent("ingredient", Ingredient.CODEC);
	public static final IngredientComponent NON_EMPTY_INGREDIENT = new IngredientComponent("non_empty_ingredient", Ingredient.CODEC_NONEMPTY);

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
		return IngredientJS.isIngredientLike(from);
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Ingredient value, ReplacementMatchInfo match) {
		return match.match() instanceof ItemMatch m && !value.isEmpty() && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(Ingredient value) {
		return value.isEmpty();
	}

	@Override
	public RecipeComponent<TinyMap<Character, Ingredient>> asPatternKey() {
		return MapRecipeComponent.INGREDIENT_PATTERN_KEY;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Ingredient value) {
		var tag = IngredientWrapper.tagKeyOf(value);

		if (tag != null) {
			builder.append(tag.location());
		} else {
			var first = value.kjs$getFirst();

			if (!first.isEmpty()) {
				builder.append(first.kjs$getIdLocation());
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
