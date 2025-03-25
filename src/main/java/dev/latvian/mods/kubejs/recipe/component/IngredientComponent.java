package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record IngredientComponent(RecipeComponentType<?> type, Codec<Ingredient> codec) implements RecipeComponent<Ingredient> {
	public static final RecipeComponentType<Ingredient> INGREDIENT = RecipeComponentType.unit(KubeJS.id("ingredient"), type -> new IngredientComponent(type, Ingredient.CODEC));
	public static final RecipeComponentType<Ingredient> NON_EMPTY_INGREDIENT = RecipeComponentType.unit(KubeJS.id("non_empty_ingredient"), type -> new IngredientComponent(type, Ingredient.CODEC_NONEMPTY));

	public static final RecipeComponentType<List<Ingredient>> UNWRAPPED_INGREDIENT_LIST = RecipeComponentType.unit(KubeJS.id("unwrapped_ingredient_list"), new RecipeComponentWithParent<>() {
		private static final RecipeComponent<List<Ingredient>> PARENT = INGREDIENT.instance().asList();
		private static final TypeInfo WRAP_TYPE = TypeInfo.RAW_LIST.withParams(TypeInfo.of(SizedIngredient.class));

		@Override
		public RecipeComponentType<?> type() {
			return UNWRAPPED_INGREDIENT_LIST;
		}

		@Override
		public RecipeComponent<List<Ingredient>> parentComponent() {
			return PARENT;
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
	});

	@Override
	public TypeInfo typeInfo() {
		return IngredientWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return IngredientWrapper.isIngredientLike(from);
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Ingredient value, ReplacementMatchInfo match) {
		return match.match() instanceof ItemMatch m && !value.isEmpty() && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(Ingredient value) {
		if (value.isEmpty()) {
			return true;
		}

		var stacks = value.getItems();

		if (stacks.length == 0) {
			return true;
		}

		int count = 0;

		for (var stack : stacks) {
			if (!stack.isEmpty() && stack.getItem() != Items.BARRIER) {
				count++;
			}
		}

		return count == 0;
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
		return "ingredient";
	}
}
