package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class JEISubtypesKubeEvent implements KubeEvent {
	public record DataComponentTypeInterpreter(DataComponentType<?>[] keys) implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack stack, UidContext context) {
			if (keys.length == 1) {
				var o = stack.getComponents().get(keys[0]);
				return o == null ? "" : o.toString();
			} else {
				var sb = new StringBuilder();
				boolean first = true;

				for (var key : keys) {
					if (first) {
						first = false;
					} else {
						sb.append('_');
					}

					var o = stack.getComponents().get(key);

					if (o != null) {
						sb.append(o);
					} else {
						sb.append("null");
					}
				}

				return sb.toString();
			}
		}
	}

	private final ISubtypeRegistration registration;

	public JEISubtypesKubeEvent(ISubtypeRegistration r) {
		registration = r;
	}

	public void registerInterpreter(Item item, SubtypeInterpreter interpreter) {
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, (stack, context) -> {
			var o = interpreter.apply(stack);
			return o == null ? "" : o.toString();
		});
	}

	public void useComponents(Ingredient items, DataComponentType<?>[] keys) {
		var in = new DataComponentTypeInterpreter(keys);

		for (var item : items.kjs$getItemTypes()) {
			registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, in);
		}
	}
}