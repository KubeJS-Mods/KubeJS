package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

@Info("""
	Invoked after all items are registered to modify them.
	""")
public class ItemModificationKubeEvent implements KubeEvent {

	@Info("""
		Modifies items matching the given ingredient.
					
		**NOTE**: tag ingredients are not supported at this time.
		""")
	public void modify(Ingredient in, Consumer<ItemModifications> c) {
		for (var item : in.kjs$getItemTypes()) {
			c.accept(new ItemModifications(item));
		}
	}

	public record ItemModifications(Item item) {
		public void setBurnTime(int i) {
			// FuelRegistry.register(i, (Item) this);
		}

		public <T> void overrideComponent(DataComponentType<T> type, T value) {
			item.kjs$overrideComponent(type, value);
		}

		public void setCraftingRemainder(Item item) {
			this.item.kjs$setCraftingRemainder(item);
		}

		public void setMaxStackSize(int size) {
			overrideComponent(DataComponents.MAX_STACK_SIZE, size);
		}

		public void setTier(Consumer<MutableToolTier> c) {
			if (item instanceof TieredItem tiered) {
				tiered.tier = Util.make(new MutableToolTier(tiered.tier), c);
			} else {
				throw new IllegalArgumentException("Item is not a tool/tiered item!");
			}
		}

		public void setNameKey(String key) {
			item.kjs$setNameKey(key);
		}
	}
}
