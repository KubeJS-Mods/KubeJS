package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record SizedIngredientComponent(RecipeComponentType<?> type, Codec<SizedIngredient> codec) implements RecipeComponent<SizedIngredient> {
	public static final RecipeComponentType<SizedIngredient> FLAT = RecipeComponentType.unit(KubeJS.id("flat_sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.FLAT_CODEC));
	public static final RecipeComponentType<SizedIngredient> NESTED = RecipeComponentType.unit(KubeJS.id("nested_sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.NESTED_CODEC));

	@Override
	public TypeInfo typeInfo() {
		return SizedIngredientWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return IngredientWrapper.isIngredientLike(from);
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, SizedIngredient value, ReplacementMatchInfo match) {
		return match.match() instanceof ItemMatch m && m.matches(cx, value.ingredient(), match.exact());
	}

	@Override
	public boolean isEmpty(SizedIngredient value) {
		return value.count() <= 0 || value.ingredient().isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, SizedIngredient value) {
		var tag = IngredientWrapper.tagKeyOf(value.ingredient());

		if (tag != null) {
			builder.append(tag.location());
		} else {
			var first = value.ingredient().kjs$getFirst();

			if (!first.isEmpty()) {
				builder.append(first.kjs$getIdLocation());
			}
		}
	}

	@Override
	public String toString() {
		return "sized_ingredient";
	}
}
