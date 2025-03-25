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

public class FluidIngredientComponent implements RecipeComponent<FluidIngredient> {
	public static final RecipeComponentType<FluidIngredient> FLUID_INGREDIENT = RecipeComponentType.unit(KubeJS.id("fluid_ingredient"), new FluidIngredientComponent());

	@Override
	public RecipeComponentType<?> type() {
		return FLUID_INGREDIENT;
	}

	@Override
	public Codec<FluidIngredient> codec() {
		return FluidIngredient.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.INGREDIENT_TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, FluidIngredient value, ReplacementMatchInfo match) {
		return match.match() instanceof FluidMatch m && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(FluidIngredient value) {
		return value.isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, FluidIngredient value) {
		if (!value.isEmpty()) {
			builder.append(value.getStacks()[0].getFluid().kjs$getIdLocation());
		}
	}

	@Override
	public String toString() {
		return "fluid_ingredient";
	}
}
