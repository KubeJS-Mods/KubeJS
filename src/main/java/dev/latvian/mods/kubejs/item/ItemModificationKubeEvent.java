package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.ComponentFunctions;
import dev.latvian.mods.kubejs.component.ItemComponentFunctions;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Info("""
	Invoked after all items are registered to modify them.
	""")
public class ItemModificationKubeEvent implements KubeEvent {
	@Info("""
		Modifies items matching the given ingredient.
		
		**NOTE**: tag ingredients are not supported at this time.
		""")
	public void modify(ItemPredicate in, Consumer<ItemModifications> c) {
		in.kjs$getItemTypes().stream().map(ItemModifications::new).forEach(c);
	}

	@RemapPrefixForJS("kjs$")
	public record ItemModifications(Item item) implements ItemComponentFunctions {
		@HideFromJS
		public static final Map<Item, Long> BURN_TIME_OVERRIDES = new IdentityHashMap<>();

		@Override
		public DataComponentMap kjs$getComponentMap() {
			return item.components();
		}

		@Override
		@HideFromJS
		public <T> ComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
			item.kjs$overrideComponent(type, value);
			return this;
		}

		public void setBurnTime(TickDuration i) {
			BURN_TIME_OVERRIDES.put(item, i.ticks());
		}

		public void setCraftingRemainder(Item item) {
			this.item.kjs$setCraftingRemainder(item);
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
