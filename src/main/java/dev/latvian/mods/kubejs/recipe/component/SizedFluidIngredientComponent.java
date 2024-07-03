package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class SizedFluidIngredientComponent implements RecipeComponent<SizedFluidIngredient> {
	public static final SizedFluidIngredientComponent FLAT = new SizedFluidIngredientComponent("flat_sized_fluid_ingredient", SizedFluidIngredient.FLAT_CODEC);
	public static final SizedFluidIngredientComponent NESTED = new SizedFluidIngredientComponent("nested_sized_fluid_ingredient", SizedFluidIngredient.NESTED_CODEC);

	public final String name;
	public final Codec<SizedFluidIngredient> codec;

	public SizedFluidIngredientComponent(String name, Codec<SizedFluidIngredient> codec) {
		this.name = name;
		this.codec = codec;
	}

	@Override
	public Codec<SizedFluidIngredient> codec() {
		return codec;
	}

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
	public String checkEmpty(RecipeKey<SizedFluidIngredient> key, SizedFluidIngredient value) {
		if (value.ingredient().isEmpty()) {
			return "SizedIngredient '" + key.name + "' can't be empty!";
		}

		return "";
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
		return name;
	}
}
