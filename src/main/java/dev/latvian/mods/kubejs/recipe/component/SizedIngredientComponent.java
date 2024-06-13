package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.bindings.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

public class SizedIngredientComponent implements RecipeComponent<SizedIngredient> {
	public static final SizedIngredientComponent FLAT = new SizedIngredientComponent("flat_sized_ingredient", SizedIngredient.FLAT_CODEC);
	public static final SizedIngredientComponent NESTED = new SizedIngredientComponent("nested_sized_ingredient", SizedIngredient.NESTED_CODEC);

	public final String name;
	public final Codec<SizedIngredient> codec;

	public SizedIngredientComponent(String name, Codec<SizedIngredient> codec) {
		this.name = name;
		this.codec = codec;
	}

	@Override
	public Codec<SizedIngredient> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return SizedIngredientWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof SizedIngredient || IngredientJS.isIngredientLike(from);
	}

	@Override
	public boolean matches(KubeRecipe recipe, SizedIngredient value, ReplacementMatch match) {
		return match instanceof ItemMatch m && m.contains(value.ingredient());
	}

	@Override
	public String checkEmpty(RecipeKey<SizedIngredient> key, SizedIngredient value) {
		if (value.ingredient().isEmpty()) {
			return "SizedIngredient '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	@Nullable
	public String createUniqueId(SizedIngredient value) {
		var item = value == null ? null : value.ingredient().kjs$getFirst();
		return item == null || item.isEmpty() ? null : RecipeSchema.normalizeId(item.kjs$getId()).replace('/', '_');
	}

	@Override
	public String toString() {
		return name;
	}
}
