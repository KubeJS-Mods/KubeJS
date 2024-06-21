package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.Arrays;
import java.util.List;

public class REIRegisterFluidSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	private final FluidComparatorRegistry registry;

	public REIRegisterFluidSubtypesKubeEvent(FluidComparatorRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		var in = (FluidIngredient) RecipeViewerEntryType.FLUID.wrapPredicate(cx, filter);
		registry.register((ctx, stack) -> {
			var result = interpreter.apply(stack);

			if (result == null) {
				return 0L;
			} else if (result instanceof Number n) {
				return Double.doubleToLongBits(n.doubleValue());
			} else {
				return result.hashCode();
			}
		}, Arrays.stream(in.getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new));
	}

	@Override
	public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
		var in = (FluidIngredient) RecipeViewerEntryType.FLUID.wrapPredicate(cx, filter);
		registry.register((EntryComparator) DataComponentComparator.of(components), Arrays.stream(in.getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new));
	}
}
