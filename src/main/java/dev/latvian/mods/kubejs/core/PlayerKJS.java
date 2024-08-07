package dev.latvian.mods.kubejs.core;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.item.ItemHandlerUtils;
import dev.latvian.mods.kubejs.kgui.action.KGUIActions;
import dev.latvian.mods.kubejs.player.KubeJSInventoryListener;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface PlayerKJS extends LivingEntityKJS, DataSenderKJS, WithAttachedData<Player> {
	@Override
	default Player kjs$self() {
		return (Player) this;
	}

	default Stages kjs$getStages() {
		throw new NoMixinException();
	}

	default PlayerStatsJS kjs$getStats() {
		throw new NoMixinException();
	}

	default boolean kjs$isMiningBlock() {
		throw new NoMixinException();
	}

	@Override
	default boolean kjs$isPlayer() {
		return true;
	}

	default boolean kjs$isFake() {
		return this instanceof FakePlayer;
	}

	@Override
	default GameProfile kjs$getProfile() {
		return kjs$self().getGameProfile();
	}

	default InventoryKJS kjs$getInventory() {
		throw new NoMixinException();
	}

	default InventoryKJS kjs$getCraftingGrid() {
		throw new NoMixinException();
	}

	default void kjs$sendInventoryUpdate() {
		kjs$self().getInventory().setChanged();
		kjs$self().inventoryMenu.getCraftSlots().setChanged();
		kjs$self().inventoryMenu.broadcastChanges();
	}

	default void kjs$give(ItemStack item) {
		ItemHandlerUtils.giveItemToPlayer(kjs$self(), item, -1);
	}

	default void kjs$giveInHand(ItemStack item) {
		ItemHandlerUtils.giveItemToPlayer(kjs$self(), item, kjs$getSelectedSlot());
	}

	default int kjs$getSelectedSlot() {
		return kjs$self().getInventory().selected;
	}

	default void kjs$setSelectedSlot(int index) {
		kjs$self().getInventory().selected = Mth.clamp(index, 0, 8);
	}

	default ItemStack kjs$getMouseItem() {
		return kjs$self().containerMenu.getCarried();
	}

	default void kjs$setMouseItem(ItemStack item) {
		kjs$self().containerMenu.setCarried(item);
	}

	@Override
	default void kjs$setStatusMessage(Component message) {
		kjs$self().displayClientMessage(message, true);
	}

	@Override
	default void kjs$spawn() {
	}

	default void kjs$addFood(int f, float m) {
		kjs$self().getFoodData().eat(f, m);
	}

	default int kjs$getFoodLevel() {
		return kjs$self().getFoodData().getFoodLevel();
	}

	default void kjs$setFoodLevel(int foodLevel) {
		kjs$self().getFoodData().setFoodLevel(foodLevel);
	}

	default float kjs$getSaturation() {
		return kjs$self().getFoodData().getSaturationLevel();
	}

	default void kjs$setSaturation(float saturation) {
		kjs$self().getFoodData().setSaturation(saturation);
	}

	default void kjs$addExhaustion(float exhaustion) {
		kjs$self().causeFoodExhaustion(exhaustion);
	}

	default void kjs$addXP(int xp) {
		kjs$self().giveExperiencePoints(xp);
	}

	default void kjs$addXPLevels(int l) {
		kjs$self().giveExperienceLevels(l);
	}

	default void kjs$setXp(int xp) {
		kjs$self().totalExperience = 0;
		kjs$self().experienceProgress = 0F;
		kjs$self().experienceLevel = 0;
		kjs$self().giveExperiencePoints(xp);
	}

	default int kjs$getXp() {
		return kjs$self().totalExperience;
	}

	default void kjs$setXpLevel(int l) {
		kjs$self().totalExperience = 0;
		kjs$self().experienceProgress = 0F;
		kjs$self().experienceLevel = 0;
		kjs$self().giveExperienceLevels(l);
	}

	default int kjs$getXpLevel() {
		return kjs$self().experienceLevel;
	}

	default void kjs$boostElytraFlight() {
		if (kjs$self().isFallFlying()) {
			var v = kjs$self().getLookAngle();
			var d0 = 1.5D;
			var d1 = 0.1D;
			var m = kjs$self().getDeltaMovement();
			kjs$self().setDeltaMovement(m.add(v.x * d1 + (v.x * d0 - m.x) * 0.5D, v.y * d1 + (v.y * d0 - m.y) * 0.5D, v.z * d1 + (v.z * d0 - m.z) * 0.5D));
		}
	}

	default AbstractContainerMenu kjs$getOpenInventory() {
		return kjs$self().containerMenu;
	}

	default void kjs$addItemCooldown(Item item, int ticks) {
		kjs$self().getCooldowns().addCooldown(item, ticks);
	}

	default KubeJSInventoryListener kjs$getInventoryChangeListener() {
		throw new NoMixinException();
	}

	default void kjs$notify(NotificationToastData builder) {
		throw new NoMixinException();
	}

	default void kjs$notify(Component title, @Nullable Component text) {
		kjs$notify(NotificationToastData.ofTitle(title, text));
	}

	default KGUIActions kjs$getKgui() {
		throw new NoMixinException();
	}
}
