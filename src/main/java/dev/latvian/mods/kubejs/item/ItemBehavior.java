package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class ItemBehavior {
	public transient boolean glow = false;
	public transient final List<Component> tooltip = new ArrayList<>();
	@Nullable
	public transient Function<ItemStack, KubeColor> barColor;
	@Nullable
	public transient ToIntFunction<ItemStack> barWidth;
	@Nullable
	public transient ItemBuilder.NameCallback nameGetter;
	@Nullable
	public transient UseAnim anim;
	@Nullable
	public transient ToIntBiFunction<ItemStack, LivingEntity> useDuration;
	@Nullable
	public transient ItemBuilder.UseCallback use;
	@Nullable
	public transient ItemBuilder.FinishUsingCallback finishUsing;
	@Nullable
	public transient ItemBuilder.ReleaseUsingCallback releaseUsing;
	@Nullable
	public transient Predicate<ItemBuilder.HurtEnemyContext> hurtEnemy;
	@Nullable
	public transient Consumer<FoodEatenKubeEvent> foodEaten;
	@Nullable
	public transient Component displayName;
	public transient boolean formattedDisplayName = false;
}
