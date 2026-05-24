package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.color.KubeColor;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

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
		public <T> ItemComponentFunctions kjs$override(DataComponentType<T> type, @Nullable T value) {
			item.kjs$overrideComponent(type, value);
			return this;
		}

		public void setBurnTime(TickDuration i) {
			BURN_TIME_OVERRIDES.put(item, i.intTicks());
		}

		public void setCraftingRemainder(Item item) {
			this.item.kjs$setCraftingRemainder(item);
		}

		public void setTier(Consumer<MutableToolTier> builder) {
			if (item instanceof TieredItem tiered) {
				var oldTier = tiered.tier;
				var tier = Util.make(new MutableToolTier(tiered.tier), builder);
				tiered.tier = tier;

				// need to update modifiers for attack dmg; this is quite messy but oh well
				var modifiers = ItemAttributeModifiers.builder();
				for (var entry : kjs$getAttributeModifiers().modifiers()) {
					if (entry.matches(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_ID)) {
						double base = entry.modifier().amount() - oldTier.getAttackDamageBonus();
						modifiers.add(entry.attribute(),
							new AttributeModifier(BASE_ATTACK_DAMAGE_ID, base + tier.getAttackDamageBonus(),
								AttributeModifier.Operation.ADD_VALUE), entry.slot());
					} else {
						modifiers.add(entry.attribute(), entry.modifier(), entry.slot());
					}
				}
				kjs$setAttributeModifiers(modifiers.build());

				kjs$setMaxDamage(tier.getUses());
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

		@Info("Makes the item glow like enchanted, even if it's not enchanted.")
		public void glow(boolean v) {
			getOrCreateBehavior().glow = v;
		}

		@Info("Adds a tooltip line to the item.")
		public void tooltip(Component text) {
			getOrCreateBehavior().tooltip.add(text);
		}

		@Info("Determines the color of the item's durability bar. Defaulted to vanilla behavior.")
		public void barColor(Function<ItemStack, KubeColor> barColor) {
			getOrCreateBehavior().barColor = barColor;
		}

		@Info("""
			Determines the width of the item's durability bar. Defaulted to vanilla behavior.
			
			The function should return a value between 0 and 13 (max width of the bar).
			""")
		public void barWidth(ToIntFunction<ItemStack> barWidth) {
			getOrCreateBehavior().barWidth = barWidth;
		}

		@Info("Sets the item's name dynamically.")
		public void name(ItemBuilder.NameCallback nameGetter) {
			getOrCreateBehavior().nameGetter = nameGetter;
		}

		@Info("Determines the animation of the item when used, e.g. eating food.")
		public void useAnimation(UseAnim anim) {
			getOrCreateBehavior().anim = anim;
		}

		@Info("""
			The duration when the item is used.
			
			For example, when eating food, this is the time it takes to eat the food.
			This can change the eating speed, or be used for other things (like making a custom bow).
			""")
		public void useDuration(ToIntBiFunction<ItemStack, LivingEntity> useDuration) {
			getOrCreateBehavior().useDuration = useDuration;
		}

		@Info("""
			Determines if the player will start using the item.
			
			For example, when eating food, returning true will make the player start eating the food.
			""")
		public void use(ItemBuilder.UseCallback use) {
			getOrCreateBehavior().use = use;
		}

		@Info("""
			Called when the player finishes using the item.
			
			This is called only when `useDuration` ticks have passed.
			
			For example, when eating food, this is called when the player has finished eating the food.
			""")
		public void finishUsing(ItemBuilder.FinishUsingCallback finishUsing) {
			getOrCreateBehavior().finishUsing = finishUsing;
		}

		@Info("""
			Called when the player released the right mouse button before finishing using the item.
			
			An example is the bow, where the arrow is shot when the player releases the right mouse button.
			""")
		public void releaseUsing(ItemBuilder.ReleaseUsingCallback releaseUsing) {
			getOrCreateBehavior().releaseUsing = releaseUsing;
		}

		@Info("""
			Called when the item is used to hurt an entity.
			
			For example, when using a sword to hit a mob, this is called.
			""")
		public void hurtEnemy(Predicate<ItemBuilder.HurtEnemyContext> hurtEnemy) {
			getOrCreateBehavior().hurtEnemy = hurtEnemy;
		}

		@Info("Called when the player finishes eating food.")
		public void foodEaten(Consumer<FoodEatenKubeEvent> foodEaten) {
			getOrCreateBehavior().foodEaten = foodEaten;
		}

		private ItemBehavior getOrCreateBehavior() {
			var b = item.kjs$getItemBehavior();
			if (b == null) {
				b = new ItemBehavior();
				item.kjs$setItemBehavior(b);
			}
			return b;
		}
	}
}
