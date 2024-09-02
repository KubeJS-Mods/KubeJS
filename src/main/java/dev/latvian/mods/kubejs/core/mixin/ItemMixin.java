package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Mixin(value = Item.class, priority = 1001)
@RemapPrefixForJS("kjs$")
public abstract class ItemMixin implements ItemKJS {
	@Shadow
	private DataComponentMap components;

	@Shadow
	@Final
	private Holder.Reference<Item> builtInRegistryHolder;

	@Unique
	private ItemBuilder kjs$itemBuilder;

	@Unique
	private Map<String, Object> kjs$typeData;

	@Unique
	private Ingredient kjs$asIngredient;

	@Unique
	private ItemStackKey kjs$typeItemStackKey;

	@Unique
	private ResourceKey<Item> kjs$registryKey;

	@Unique
	private String kjs$id;

	@Override
	@Nullable
	public ItemBuilder kjs$getItemBuilder() {
		return kjs$itemBuilder;
	}

	@Override
	public Holder.Reference<Item> kjs$asHolder() {
		return builtInRegistryHolder;
	}

	@Override
	public ResourceKey<Item> kjs$getKey() {
		if (kjs$registryKey == null) {
			kjs$registryKey = ItemKJS.super.kjs$getKey();
		}

		return kjs$registryKey;
	}

	@Override
	public String kjs$getId() {
		if (kjs$id == null) {
			kjs$id = ItemKJS.super.kjs$getId();
		}

		return kjs$id;
	}

	@Override
	public void kjs$setItemBuilder(ItemBuilder b) {
		kjs$itemBuilder = b;
	}

	@Override
	public Map<String, Object> kjs$getTypeData() {
		if (kjs$typeData == null) {
			kjs$typeData = new HashMap<>();
		}

		return kjs$typeData;
	}

	@Override
	@HideFromJS
	public <T> void kjs$overrideComponent(DataComponentType<T> type, @Nullable T value) {
		var builder = DataComponentMap.builder().addAll(this.components);
		builder.set(type, value);
		this.components = Item.Properties.COMPONENT_INTERNER.intern(Item.Properties.validateComponents(builder.build()));
	}

	@Override
	@Accessor("craftingRemainingItem")
	@Mutable
	public abstract void kjs$setCraftingRemainder(Item i);

	@Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
	private void isFoil(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.glow) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "appendHoverText", at = @At("RETURN"))
	private void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
		if (kjs$itemBuilder != null && !kjs$itemBuilder.tooltip.isEmpty()) {
			tooltip.addAll(kjs$itemBuilder.tooltip);
		}
	}

	@Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
	private void isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barWidth != null && kjs$itemBuilder.barWidth.applyAsInt(stack) <= Item.MAX_BAR_WIDTH) {
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	private void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barWidth != null) {
			ci.setReturnValue(kjs$itemBuilder.barWidth.applyAsInt(stack));
		}
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	private void getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.barColor != null) {
			ci.setReturnValue(kjs$itemBuilder.barColor.apply(stack).kjs$getRGB());
		}
	}

	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void getUseDuration(ItemStack itemStack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.useDuration != null) {
			cir.setReturnValue(kjs$itemBuilder.useDuration.applyAsInt(itemStack, entity));
		}
	}

	@Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
	private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.anim != null) {
			ci.setReturnValue(kjs$itemBuilder.anim);
		}
	}

	@Inject(method = "getName", at = @At("HEAD"), cancellable = true)
	private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.nameGetter != null) {
			ci.setReturnValue(kjs$itemBuilder.nameGetter.apply(itemStack));
		}

		if (kjs$itemBuilder != null && kjs$itemBuilder.displayName != null && kjs$itemBuilder.formattedDisplayName) {
			ci.setReturnValue(kjs$itemBuilder.displayName);
		}
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.use != null) {
			ItemStack itemStack = player.getItemInHand(interactionHand);
			if (kjs$itemBuilder.use.use(level, player, interactionHand)) {
				ci.setReturnValue(ItemUtils.startUsingInstantly(level, player, interactionHand));
			} else {
				ci.setReturnValue(InteractionResultHolder.fail(itemStack));
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
	private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.finishUsing != null) {
			ci.setReturnValue(kjs$itemBuilder.finishUsing.finishUsingItem(itemStack, level, livingEntity));
		}
	}

	@Inject(method = "releaseUsing", at = @At("HEAD"))
	private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.releaseUsing != null) {
			kjs$itemBuilder.releaseUsing.releaseUsing(itemStack, level, livingEntity, i);
		}
	}

	@Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
	private void hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
		if (kjs$itemBuilder != null && kjs$itemBuilder.hurtEnemy != null) {
			cir.setReturnValue(kjs$itemBuilder.hurtEnemy.test(new ItemBuilder.HurtEnemyContext(itemStack, livingEntity, livingEntity2)));
		}
	}

	@Override
	public Ingredient kjs$asIngredient() {
		if (kjs$asIngredient == null) {
			var is = new ItemStack(kjs$self());
			kjs$asIngredient = is.isEmpty() ? Ingredient.EMPTY : Ingredient.of(Stream.of(is));
		}

		return kjs$asIngredient;
	}

	@Override
	@Accessor("descriptionId")
	@Mutable
	public abstract void kjs$setNameKey(String key);

	@Override
	public ItemStackKey kjs$getTypeItemStackKey() {
		if (kjs$typeItemStackKey == null) {
			kjs$typeItemStackKey = new ItemStackKey(kjs$self(), null);
		}

		return kjs$typeItemStackKey;
	}

	@Override
	@Accessor("canRepair")
	@Mutable
	public abstract void kjs$setCanRepair(boolean repairable);
}
