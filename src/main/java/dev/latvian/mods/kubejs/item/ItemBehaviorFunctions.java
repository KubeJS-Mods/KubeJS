package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

@ReturnsSelf
public interface ItemBehaviorFunctions {
	@HideFromJS
	ItemBehavior kjs$getOrCreateBehavior();

	@HideFromJS // Used only in item builders
	default ItemBehaviorFunctions displayName(Component component, boolean formattedDisplayName) {
		ItemBehavior behavior = kjs$getOrCreateBehavior();
		behavior.displayName = component;
		behavior.formattedDisplayName = formattedDisplayName;
		return this;
	}

	@Info("Makes the item glow like enchanted, even if it's not enchanted.")
	default ItemBehaviorFunctions glow(boolean glow) {
		kjs$getOrCreateBehavior().glow = glow;
		return this;
	}

	@Info("Adds a tooltip to the item.")
	default ItemBehaviorFunctions tooltip(Component component) {
		kjs$getOrCreateBehavior().tooltip.add(component);
		return this;
	}

	@Info("Determines the color of the item's durability bar. Defaulted to vanilla behavior.")
	default ItemBehaviorFunctions barColor(Function<ItemStack, KubeColor> barColor) {
		kjs$getOrCreateBehavior().barColor = barColor;
		return this;
	}

	@Info("""
		Determines the width of the item's durability bar. Defaulted to vanilla behavior.
				
		The function should return a value between 0 and 13 (max width of the bar).
		""")
	default ItemBehaviorFunctions barWidth(ToIntFunction<ItemStack> barWidth) {
		kjs$getOrCreateBehavior().barWidth = barWidth;
		return this;
	}

	@Info("""
		Sets the item's name dynamically.
		""")
	default ItemBehaviorFunctions name(ItemBehavior.NameCallback name) {
		kjs$getOrCreateBehavior().nameGetter = name;
		return this;
	}

	@Info("Determines the animation of the item when used, e.g. eating food.")
	default ItemBehaviorFunctions useAnimation(ItemUseAnimation anim) {
		kjs$getOrCreateBehavior().anim = anim;
		return this;
	}

	@Info("""
		The duration when the item is used.
				
		For example, when eating food, this is the time it takes to eat the food.
		This can change the eating speed, or be used for other things (like making a custom bow).
		""")
	default ItemBehaviorFunctions useDuration(ToIntBiFunction<ItemStack, LivingEntity> useDuration) {
		kjs$getOrCreateBehavior().useDuration = useDuration;
		return this;
	}

	@Info("""
		Determines if player will start using the item.
				
		For example, when eating food, returning true will make the player start eating the food.
		""")
	default ItemBehaviorFunctions use(ItemBehavior.UseCallback use) {
		kjs$getOrCreateBehavior().use = use;
		return this;
	}

	@Info("""
		When players finish using the item.
				
		This is called only when `useDuration` ticks have passed.
				
		For example, when eating food, this is called when the player has finished eating the food, so hunger is restored.
		""")
	default ItemBehaviorFunctions finishUsing(ItemBehavior.FinishUsingCallback finishUsing) {
		kjs$getOrCreateBehavior().finishUsing = finishUsing;
		return this;
	}

	@Info("""
		When players did not finish using the item but released the right mouse button halfway through.
				
		An example is the bow, where the arrow is shot when the player releases the right mouse button.
				
		To ensure the bow won't finish using, Minecraft sets the `useDuration` to a very high number (1h).
		""")
	default ItemBehaviorFunctions releaseUsing(ItemBehavior.ReleaseUsingCallback releaseUsing) {
		kjs$getOrCreateBehavior().releaseUsing = releaseUsing;
		return this;
	}

	@Info("""
		Gets called when the item is used to hurt an entity.
				
		For example, when using a sword to hit a mob, this is called.
		""")
	default ItemBehaviorFunctions hurtEnemy(Predicate<ItemBehavior.HurtEnemyContext> hurtEnemy) {
		kjs$getOrCreateBehavior().hurtEnemy = hurtEnemy;
		return this;
	}

	@HideFromJS // Used only in item builders
	default ItemBehaviorFunctions foodEaten(Consumer<FoodEatenKubeEvent> foodEaten) {
		kjs$getOrCreateBehavior().foodEaten = foodEaten;
		return this;
	}

	@Info("""
		Determines if piglins will give an item or something in exchange for the item.
		""")
	default ItemBehaviorFunctions isPiglinCurrency(Predicate<ItemStack> isPiglinCurrency) {
		kjs$getOrCreateBehavior().isPiglinCurrency = isPiglinCurrency;
		return this;
	}

	default ItemBehaviorFunctions isPiglinCurrency(boolean isPiglinCurrency) {
		return isPiglinCurrency(stack -> isPiglinCurrency);
	}

	@Info("""
		Whether this item can be used to hide the player's gaze from Endermen and Creakings.
		""")
	default ItemBehaviorFunctions protectFromGaze(ItemBehavior.GazeDisguiseTest isGazeDisguise) {
		kjs$getOrCreateBehavior().protectFromGaze = isGazeDisguise;
		return this;
	}

	default ItemBehaviorFunctions protectFromGaze(boolean isGazeDisguise) {
		return protectFromGaze((stack, player, entity) -> isGazeDisguise);
	}

	@Info("""
		Determines if piglins will be neutral to the wearer of the item and will not attack on sight.
				
		However, this does not prevent piglins from being hostile due to other actions, or make piglins
		stop being hostile if they are already hostile.
		""")
	default ItemBehaviorFunctions makesPiglinsNeutral(BiPredicate<ItemStack, LivingEntity> makesPiglinsNeutral) {
		kjs$getOrCreateBehavior().makesPiglinsNeutral = makesPiglinsNeutral;
		return this;
	}

	default ItemBehaviorFunctions makesPiglinsNeutral(boolean makesPiglinsNeutral) {
		return makesPiglinsNeutral((stack, wearer) -> makesPiglinsNeutral);
	}

	@Info("""
		Returns the item that remains in the crafting grid (or furnace fuel) after crafting with this item programatically.
				
		Returning an empty stack or null will make the item be consumed as normal.
				
		An example would be durability-consuming items, e.g. hammers or wrenches.
		""")
	default ItemBehaviorFunctions craftingRemainingItem(UnaryOperator<ItemStack> craftingRemainingItem) {
		kjs$getOrCreateBehavior().craftingRemainingItem = craftingRemainingItem;
		return this;
	}

	@Info("""
		Determines the lifespan in ticks of the item when it's dropped on the ground as an entity.
				
		Used for items like Fluix Seeds to prevent them from despawning.
		""")
	default ItemBehaviorFunctions getEntityLifespan(ToIntBiFunction<ItemStack, Level> getEntityLifespan) {
		kjs$getOrCreateBehavior().getEntityLifespan = getEntityLifespan;
		return this;
	}

	default ItemBehaviorFunctions getEntityLifespan(int lifespan) {
		return getEntityLifespan((stack, level) -> lifespan);
	}

	@Info("""
		Determines if the player can walk on powdered snow with this item worn in the feet slot.
		""")
	default ItemBehaviorFunctions canWalkOnPowderedSnow(BiPredicate<ItemStack, LivingEntity> canWalkOnPowderedSnow) {
		kjs$getOrCreateBehavior().canWalkOnPowderedSnow = canWalkOnPowderedSnow;
		return this;
	}

	default ItemBehaviorFunctions canWalkOnPowderedSnow(boolean canWalkOnPowderedSnow) {
		return canWalkOnPowderedSnow((stack, wearer) -> canWalkOnPowderedSnow);
	}

	@Info("""
		Determines if the item can perform corresponding action. E.g. shearing sheep, stripping logs, etc.
		""")
	default ItemBehaviorFunctions canPerformAction(BiPredicate<ItemStack, ItemAbility> canPerformAction) {
		kjs$getOrCreateBehavior().canPerformAction = canPerformAction;
		return this;
	}

	@Info("""
		Determines if the item entity will be destroyed by the damage source.
				
		For example, netherite items will not be destroyed by lava.
		""")
	default ItemBehaviorFunctions canBeHurtBy(BiPredicate<ItemStack, DamageSource> canBeHurtBy) {
		kjs$getOrCreateBehavior().canBeHurtBy = canBeHurtBy;
		return this;
	}

	@Info("""
		Determines if the item entity will be destroyed by listed damages types.
				
		All other damages will be treated as normal. Passing [] will make the item immune to all damage.
		"""
	)
	default ItemBehaviorFunctions onlyHurtBy(List<ResourceKey<DamageType>> damageTypes) {
		return canBeHurtBy((stack, source) -> {
			for (ResourceKey<DamageType> type : damageTypes) {
				if (source.is(type)) {
					return true;
				}
			}
			return false;
		});
	}

	@Info("""
		Determines if the item entity will be immune to listed damages types.
				
		All other damages will be treated as normal. Passing [] will have no effect.
		""")
	default ItemBehaviorFunctions immuneTo(List<ResourceKey<DamageType>> damageTypes) {
		if (damageTypes.isEmpty()) {
			return this;
		}

		return canBeHurtBy((stack, source) -> {
			for (ResourceKey<DamageType> type : damageTypes) {
				if (source.is(type)) {
					return false;
				}
			}
			return true;
		});
	}

	@Info("""
		Returns the enchanted item stack after applying enchantments to the item.
				
		For example, books will be transformed into enchanted books by using this.
		""")
	default ItemBehaviorFunctions applyEnchantments(BiFunction<ItemStack, List<EnchantmentInstance>, ItemStack> applyEnchantments) {
		kjs$getOrCreateBehavior().applyEnchantments = applyEnchantments;
		return this;
	}
}
