package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import net.minecraft.core.component.DataComponentType;

public class REIRegisterFluidSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	private final FluidComparatorRegistry registry;

	public REIRegisterFluidSubtypesKubeEvent(FluidComparatorRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		ConsoleJS.CLIENT.error("WIP");
	}

	@Override
	public void useComponents(Context cx, Object filter) {
		ConsoleJS.CLIENT.error("WIP");
	}

	@Override
	public void useComponents(Context cx, Object filter, DataComponentType<?>[] components) {
		ConsoleJS.CLIENT.error("WIP");
	}
}
