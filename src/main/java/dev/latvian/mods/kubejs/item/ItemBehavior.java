package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

public class ItemBehavior {
	@Nullable
	public transient Component displayName;
	public transient boolean formattedDisplayName = false;
	public transient boolean glow = false;
	public transient final List<Component> tooltip = new ArrayList<>();

	@Nullable
	public transient ItemTintFunction tint;
	@Nullable
	public transient Function<ItemStack, KubeColor> barColor;
	@Nullable
	public transient ToIntFunction<ItemStack> barWidth;
	@Nullable
	public transient NameCallback nameGetter;
	@Nullable
	public transient ItemUseAnimation anim;
	@Nullable
	public transient ToIntBiFunction<ItemStack, LivingEntity> useDuration;
	@Nullable
	public transient UseCallback use;
	@Nullable
	public transient FinishUsingCallback finishUsing;
	@Nullable
	public transient ReleaseUsingCallback releaseUsing;
	@Nullable
	public transient Predicate<HurtEnemyContext> hurtEnemy;
	@Nullable
	public transient Consumer<FoodEatenKubeEvent> foodEaten;

	// IItemExtension methods
	// @Nullable // This is somehow bugged
	// public transient BiPredicate<ItemStack, Player> onDroppedByPlayer;
	@Nullable
	public transient Predicate<ItemStack> isPiglinCurrency;
	@Nullable
	public transient GazeDisguiseTest protectFromGaze;
	@Nullable
	public transient BiPredicate<ItemStack, LivingEntity> makesPiglinsNeutral;
	@Nullable
	public transient UnaryOperator<ItemStack> craftingRemainingItem;
	@Nullable
	public transient ToIntBiFunction<ItemStack, Level> getEntityLifespan;
	@Nullable
	public transient BiPredicate<ItemStack, LivingEntity> canWalkOnPowderedSnow;
	@Nullable
	public transient BiPredicate<ItemInstance, ItemAbility> canPerformAction;
	@Nullable
	public transient BiPredicate<ItemStack, DamageSource> canBeHurtBy;
	@Nullable
	public transient BiFunction<ItemStack, List<EnchantmentInstance>, ItemStack> applyEnchantments;


	@FunctionalInterface
	public interface UseCallback {
		boolean use(Level level, Player player, InteractionHand interactionHand);
	}

	@FunctionalInterface
	public interface FinishUsingCallback {
		ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity);
	}

	@FunctionalInterface
	public interface ReleaseUsingCallback {
		void releaseUsing(ItemStack itemStack, Level level, LivingEntity user, int tick);
	}

	@FunctionalInterface
	public interface NameCallback {
		Component apply(ItemStack itemStack);
	}

	@FunctionalInterface
	public interface GazeDisguiseTest {
		boolean test(ItemStack stack, Player player, @Nullable LivingEntity entity);
	}


	public record HurtEnemyContext(ItemStack getItem, LivingEntity getTarget, LivingEntity getAttacker) {
	}
}
