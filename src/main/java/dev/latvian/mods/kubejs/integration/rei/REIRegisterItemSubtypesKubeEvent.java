package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class REIRegisterItemSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	private final ItemComparatorRegistry registry;

	public REIRegisterItemSubtypesKubeEvent(ItemComparatorRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		var in = (ItemPredicate) RecipeViewerEntryType.ITEM.wrapPredicate(cx, filter);
		registry.register((ctx, stack) -> {
			var result = interpreter.apply(stack);

			if (result == null) {
				return 0L;
			} else if (result instanceof Number n) {
				return Double.doubleToLongBits(n.doubleValue());
			} else {
				return result.hashCode();
			}
		}, in.kjs$getItemTypes().toArray(new Item[0]));
	}

	@Override
	public void useComponents(Context cx, Object filter) {
		var in = (ItemPredicate) RecipeViewerEntryType.ITEM.wrapPredicate(cx, filter);
		registry.registerComponents(in.kjs$getItemTypes().toArray(new Item[0]));
	}

	@Override
	public void useComponents(Context cx, Object filter, DataComponentType<?>[] components) {
		var in = (ItemPredicate) RecipeViewerEntryType.ITEM.wrapPredicate(cx, filter);
		registry.register((ctx, stack) -> {
			long hash = 1L;

			for (var component : components) {
				hash = hash * 31 + (Objects.hashCode(component) ^ Objects.hashCode(stack.get(component)));
			}

			return hash;
		}, in.kjs$getItemTypes().toArray(new Item[0]));
	}
}
