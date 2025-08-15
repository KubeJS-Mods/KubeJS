package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.component.ComponentFunctions;
import dev.latvian.mods.kubejs.component.ItemComponentFunctions;
import dev.latvian.mods.kubejs.core.DiggerItemKJS;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.TickDuration;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;

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
		public static final Reference2IntOpenHashMap<Item> BURN_TIME_OVERRIDES = new Reference2IntOpenHashMap<>();

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
			BURN_TIME_OVERRIDES.put(item, i.intTicks());
		}

		public void setCraftingRemainder(Item item) {
			this.item.kjs$setCraftingRemainder(item);
		}

		public void setTier(Consumer<MutableToolTier> c) {
			if (item instanceof TieredItem tiered) {
				var oldTier = tiered.tier;
				var tier = Util.make(new MutableToolTier(tiered.tier), c);
				tiered.tier = tier;

				// need to update modifiers for attack dmg; this is quite messy but oh well
				var modifiers = ItemAttributeModifiers.builder();
				for (var entry : kjs$get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers()) {
					if (entry.matches(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_ID)) {
						double base = entry.modifier().amount() - oldTier.getAttackDamageBonus();
						modifiers.add(entry.attribute(),
							new AttributeModifier(BASE_ATTACK_DAMAGE_ID, base + tier.getAttackDamageBonus(),
								AttributeModifier.Operation.ADD_VALUE), entry.slot());
					} else {
						modifiers.add(entry.attribute(), entry.modifier(), entry.slot());
					}
				}
				kjs$override(DataComponents.ATTRIBUTE_MODIFIERS, modifiers.build());

				// if it's a digger item we also need to modify the tool properties
				if (tiered instanceof DiggerItemKJS dig) {
					kjs$setTool(tier.createToolProperties(dig.kjs$getMineableTag()));
				}
			} else {
				throw new IllegalArgumentException("Item is not a tool/tiered item!");
			}
		}

		public void setNameKey(String key) {
			item.kjs$setNameKey(key);
		}

		public void disableRepair() {
			item.kjs$setCanRepair(false);
		}
	}
}
