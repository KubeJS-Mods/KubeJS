package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class JEISubtypesEventJS extends EventJS {
	@FunctionalInterface
	public interface Interpreter extends Function<ItemStackJS, Object> {
	}

	private static class NBTKeyInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		private final String key;

		private NBTKeyInterpreter(String k) {
			key = k;
		}

		@Override
		public String apply(ItemStack stack, UidContext context) {
			CompoundTag nbt = stack.getTag();

			if (nbt == null || !nbt.contains(key)) {
				return "";
			}

			return String.valueOf(nbt.get(key));
		}
	}

	private final ISubtypeRegistration registration;

	public JEISubtypesEventJS(ISubtypeRegistration r) {
		registration = r;
	}

	public void registerInterpreter(Object id, Interpreter interpreter) {
		registration.registerSubtypeInterpreter(ItemStackJS.of(id).getItem(), (stack, context) -> {
			Object o = interpreter.apply(ItemStackJS.of(stack));
			return o == null ? "" : o.toString();
		});
	}

	public void useNBT(IngredientJS items) {
		registration.useNbtForSubtypes(items.getVanillaItems().toArray(new Item[0]));
	}

	public void useNBTKey(IngredientJS items, String key) {
		NBTKeyInterpreter in = new NBTKeyInterpreter(key);

		for (Item item : items.getVanillaItems()) {
			registration.registerSubtypeInterpreter(item, new NBTKeyInterpreter(key));
		}
	}
}