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

public record FluidStackComponent(RecipeComponentType<?> type, Codec<FluidStack> codec) implements RecipeComponent<FluidStack> {
	public static final RecipeComponentType<FluidStack> FLUID_STACK = RecipeComponentType.unit(KubeJS.id("fluid_stack"), type -> new FluidStackComponent(type, FluidStack.OPTIONAL_CODEC));
	public static final RecipeComponentType<FluidStack> STRICT_FLUID_STACK = RecipeComponentType.unit(KubeJS.id("strict_fluid_stack"), type -> new FluidStackComponent(type, FluidStack.CODEC));

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.TYPE_INFO;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, FluidStack value, ReplacementMatchInfo match) {
		return match.match() instanceof FluidMatch m && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(FluidStack value) {
		return value.isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, FluidStack value) {
		if (!value.isEmpty()) {
			builder.append(value.getFluid().kjs$getIdLocation());
		}
	}

	@Override
	public String toString() {
		return "fluid_stack";
	}
}
