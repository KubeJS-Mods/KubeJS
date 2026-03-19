package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.Nullable;

public record FluidIngredientComponent(ResourceKey<RecipeComponentType<?>> type, Codec<FluidIngredient> codec, boolean allowEmpty) implements RecipeComponent<FluidIngredient> {
	public static final FluidIngredientComponent FLUID_INGREDIENT = new FluidIngredientComponent(
		RecipeComponentType.builtin("fluid_ingredient"),
		FluidIngredient.CODEC, false
	);

	public static final FluidIngredientComponent OPTIONAL_FLUID_INGREDIENT = new FluidIngredientComponent(
		RecipeComponentType.builtin("optional_fluid_ingredient"),
		FluidIngredient.CODEC, true
	);

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.INGREDIENT_TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(RecipeMatchContext cx, FluidIngredient value, ReplacementMatchInfo match) {
		return match.match() instanceof FluidMatch m && m.matches(cx, value, match.exact());
	}

	@Override
	public boolean isEmpty(FluidIngredient value) {
		return value.fluids().isEmpty();
	}


	@Override
	public void buildUniqueId(UniqueIdBuilder builder, FluidIngredient value) {
		if (!value.fluids().isEmpty()) {
			builder.append(value.fluids().getFirst().value().kjs$getIdLocation());
		}
	}

	@Override
	public String toString() {
		return type.toString();
	}

	@Override
	public String toString(OpsContainer ops, FluidIngredient value) {
		// Better toString?
		return value.toString();
	}
}
