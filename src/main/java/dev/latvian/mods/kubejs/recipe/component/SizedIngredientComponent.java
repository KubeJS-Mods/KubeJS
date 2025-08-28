package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record SizedIngredientComponent(RecipeComponentType<?> type, Codec<SizedIngredient> codec, boolean allowEmpty) implements RecipeComponent<SizedIngredient> {
	public static final RecipeComponentType<SizedIngredient> SIZED_INGREDIENT = RecipeComponentType.unit(KubeJS.id("sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.NESTED_CODEC, false));
	public static final RecipeComponentType<SizedIngredient> OPTIONAL_SIZED_INGREDIENT = RecipeComponentType.unit(KubeJS.id("optional_sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.NESTED_CODEC, true));

	public static final RecipeComponentType<SizedIngredient> FLAT = RecipeComponentType.unit(KubeJS.id("flat_sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.FLAT_CODEC, false));
	public static final RecipeComponentType<SizedIngredient> OPTIONAL_FLAT = RecipeComponentType.unit(KubeJS.id("optional_flat_sized_ingredient"), type -> new SizedIngredientComponent(type, SizedIngredient.FLAT_CODEC, true));

	@Override
	public TypeInfo typeInfo() {
		return SizedIngredientWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, Object from) {
		return IngredientWrapper.isIngredientLike(from);
	}

	@Override
	public boolean matches(RecipeMatchContext cx, SizedIngredient value, ReplacementMatchInfo match) {
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
		return type.toString();
	}

	@Override
	public List<Ingredient> spread(SizedIngredient value) {
		int count = value.count();

		if (count <= 0) {
			return List.of();
		} else if (count == 1) {
			return List.of(value.ingredient());
		} else {
			var list = new ArrayList<Ingredient>(count);
			var in = value.ingredient();

			for (int i = 0; i < count; i++) {
				list.add(in);
			}

			return list;
		}
	}
}
