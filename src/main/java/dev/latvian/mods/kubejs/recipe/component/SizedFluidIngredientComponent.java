package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.Nullable;

public record SizedFluidIngredientComponent(ResourceKey<RecipeComponentType<?>> type, Codec<SizedFluidIngredient> codec, boolean allowEmpty) implements RecipeComponent<SizedFluidIngredient> {
	public static final SizedFluidIngredientComponent SIZED_FLUID_INGREDIENT = new SizedFluidIngredientComponent(
		RecipeComponentType.builtin("sized_fluid_ingredient"),
		SizedFluidIngredient.CODEC, false
	);

	public static final SizedFluidIngredientComponent OPTIONAL_SIZED_FLUID_INGREDIENT = new SizedFluidIngredientComponent(
		RecipeComponentType.builtin("optional_sized_fluid_ingredient"),
		SizedFluidIngredient.CODEC, true
	);

	@Override
	public TypeInfo typeInfo() {
		return FluidWrapper.SIZED_INGREDIENT_TYPE_INFO;
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof SizedFluidIngredient || from instanceof FluidIngredient || from instanceof FluidStack || from instanceof Fluid;
	}

	@Override
	public boolean matches(RecipeMatchContext cx, SizedFluidIngredient value, ReplacementMatchInfo match) {
		return match.match() instanceof FluidMatch m && m.matches(cx, value.ingredient(), match.exact());
	}

	@Override
	public boolean isEmpty(SizedFluidIngredient value) {
		return value.amount() <= 0 || value.ingredient().fluids().isEmpty();
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, SizedFluidIngredient value) {
		var fluids = value.ingredient().fluids();

		if (!fluids.isEmpty()) {
			builder.append(fluids.getFirst().value().kjs$getIdLocation());
		}
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
