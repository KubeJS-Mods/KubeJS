package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record SizedFluidIngredientComponent(RecipeComponentType<?> type, Codec<SizedFluidIngredient> codec, boolean allowEmpty) implements RecipeComponent<SizedFluidIngredient> {
	public static final RecipeComponentType<SizedFluidIngredient> FLAT = RecipeComponentType.unit(KubeJS.id("flat_sized_fluid_ingredient"), type -> new SizedFluidIngredientComponent(type, SizedFluidIngredient.FLAT_CODEC, false));
	public static final RecipeComponentType<SizedFluidIngredient> NESTED = RecipeComponentType.unit(KubeJS.id("nested_sized_fluid_ingredient"), type -> new SizedFluidIngredientComponent(type, SizedFluidIngredient.NESTED_CODEC, false));
	public static final RecipeComponentType<SizedFluidIngredient> OPTIONAL_FLAT = RecipeComponentType.unit(KubeJS.id("optional_flat_sized_fluid_ingredient"), type -> new SizedFluidIngredientComponent(type, SizedFluidIngredient.FLAT_CODEC, true));
	public static final RecipeComponentType<SizedFluidIngredient> OPTIONAL_NESTED = RecipeComponentType.unit(KubeJS.id("optional_nested_sized_fluid_ingredient"), type -> new SizedFluidIngredientComponent(type, SizedFluidIngredient.NESTED_CODEC, true));

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.SIZED_INGREDIENT_TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, SizedFluidIngredient value, ReplacementMatchInfo match) {
		return match.match() instanceof FluidMatch m && m.matches(cx, value.ingredient(), match.exact());
	}

	@Override
	public boolean isEmpty(SizedFluidIngredient value) {
		return value.amount() <= 0 || value.ingredient().isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, SizedFluidIngredient value) {
		if (!value.ingredient().isEmpty()) {
			var stacks = value.ingredient().getStacks();

			if (stacks.length > 0) {
				builder.append(stacks[0].getFluid().kjs$getIdLocation());
			}
		}
	}

	@Override
	public String toString() {
		if (allowEmpty) {
			return codec == SizedFluidIngredient.FLAT_CODEC ? "optional_flat_sized_fluid_ingredient" : "optional_nested_sized_fluid_ingredient";
		} else {
			return codec == SizedFluidIngredient.FLAT_CODEC ? "flat_sized_fluid_ingredient" : "nested_sized_fluid_ingredient";
		}
	}
}
