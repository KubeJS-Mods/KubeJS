package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

public class FluidIngredientComponent implements RecipeComponent<FluidIngredient> {
	public static final FluidIngredientComponent FLUID_INGREDIENT = new FluidIngredientComponent();

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
	public String checkEmpty(RecipeKey<FluidIngredient> key, FluidIngredient value) {
		if (value.isEmpty()) {
			return "FluidIngredient '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	@Nullable
	public String createUniqueId(FluidIngredient value) {
		return value == null || value.isEmpty() || value.hasNoFluids() ? null : RecipeSchema.normalizeId(value.getStacks()[0].getFluid().kjs$getId()).replace('/', '_');
	}

	@Override
	public String toString() {
		return "fluid_ingredient";
	}
}
