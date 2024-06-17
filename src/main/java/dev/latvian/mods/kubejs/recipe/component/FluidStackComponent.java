package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

public class FluidStackComponent implements RecipeComponent<FluidStack> {
	public static final FluidStackComponent FLUID_STACK = new FluidStackComponent();

	@Override
	public Codec<FluidStack> codec() {
		return FluidStack.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(KubeRecipe recipe, FluidStack value, ReplacementMatch match) {
		return match instanceof FluidLike m && m.contains((FluidLike) (Object) value);
	}

	@Override
	public String checkEmpty(RecipeKey<FluidStack> key, FluidStack value) {
		if (value.isEmpty()) {
			return "FluidStack '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	@Nullable
	public String createUniqueId(FluidStack value) {
		return value == null || value.isEmpty() ? null : RecipeSchema.normalizeId(value.getFluid().kjs$getId()).replace('/', '_');
	}

	@Override
	public String toString() {
		return "fluid_stack";
	}
}
