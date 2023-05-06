package dev.latvian.mods.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.item.ContainerInventory;
import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.item.ItemHandlerUtils;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.RangedWrapper;
import dev.latvian.mods.kubejs.stages.Stages;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.WithAttachedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerJS<P extends Player> extends LivingEntityJS implements WithAttachedData {
	public final P minecraftPlayer;

	private final PlayerDataJS playerData;
	private InventoryJS inventory;
	private InventoryJS craftingGrid;

	public PlayerJS(PlayerDataJS data, P player) {
		super(player);
		this.playerData = data;
		this.minecraftPlayer = player;
	}

	@Override
	public AttachedData getData() {
		return playerData.getData();
	}

	@Override
	public PlayerJS<?> getPlayer() {
		return this;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	public boolean isFake() {
		return PlayerHooks.isFake(minecraftPlayer);
	}

	public String toString() {
		return minecraftPlayer.getGameProfile().getName();
	}

	@Override
	public GameProfile getProfile() {
		return minecraftPlayer.getGameProfile();
	}

	public InventoryJS getInventory() {
		if (inventory == null) {
			inventory = new InventoryJS(minecraftPlayer.getInventory()) {
				@Override
				public void markDirty() {
					sendInventoryUpdate();
				}
			};
		}

		return inventory;
	}

	public InventoryJS getCraftingGrid() {
		if (craftingGrid == null) {
			// Wrap the crafting grid in a ranged wrapper and container inventory to prevent the clear
			// method from causing an index out of bounds exception.
			// This is a workaround for a bug in the vanilla crafting grid implementation.
			// For any mods that increase the size of the crafting grid, this won't work.
			craftingGrid = new InventoryJS(new RangedWrapper(new ContainerInventory(minecraftPlayer.inventoryMenu.getCraftSlots()), 0, 4)) {

				@Override
				public void markDirty() {
					sendInventoryUpdate();
				}
			};
		}

		return craftingGrid;
	}

	public void sendInventoryUpdate() {
		minecraftPlayer.getInventory().setChanged();
		minecraftPlayer.inventoryMenu.getCraftSlots().setChanged();
		minecraftPlayer.inventoryMenu.broadcastChanges();
	}

	public void give(ItemStackJS item) {
		ItemHandlerUtils.giveItemToPlayer(minecraftPlayer, item.getItemStack(), -1);
	}

	public void giveInHand(ItemStackJS item) {
		ItemHandlerUtils.giveItemToPlayer(minecraftPlayer, item.getItemStack(), getSelectedSlot());
	}

	public int getSelectedSlot() {
		return minecraftPlayer.getInventory().selected;
	}

	public void setSelectedSlot(int index) {
		minecraftPlayer.getInventory().selected = Mth.clamp(index, 0, 8);
	}

	public ItemStackJS getMouseItem() {
		if (minecraftPlayer.containerMenu != null) {
			return ItemStackJS.of(minecraftPlayer.containerMenu.getCarried());
		} else {
			return ItemStackJS.of(minecraftPlayer.inventoryMenu.getCarried());
		}
	}

	public void setMouseItem(ItemStackJS item) {
		if (minecraftPlayer.containerMenu != null) {
			minecraftPlayer.containerMenu.setCarried(item.getItemStack());
		} else {
			minecraftPlayer.inventoryMenu.setCarried(item.getItemStack());
		}
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		super.setPositionAndRotation(x, y, z, yaw, pitch);

		if (minecraftPlayer instanceof ServerPlayer) {
			((ServerPlayer) minecraftPlayer).connection.teleport(x, y, z, yaw, pitch);
		}
	}

	@Override
	public void setStatusMessage(Component message) {
		minecraftPlayer.displayClientMessage(message, true);
	}

	public boolean isCreativeMode() {
		return minecraftPlayer.isCreative();
	}

	public boolean isSpectator() {
		return minecraftPlayer.isSpectator();
	}

	public abstract PlayerStatsJS getStats();

	@Override
	public void spawn() {
	}

	public void sendData(String channel, @Nullable CompoundTag data) {
	}

	public void addFood(int f, float m) {
		getFoodData().eat(f, m);
	}

	public int getFoodLevel() {
		return getFoodData().getFoodLevel();
	}

	public void setFoodLevel(int foodLevel) {
		getFoodData().setFoodLevel(foodLevel);
	}

	public float getSaturation() {
		return getFoodData().getSaturationLevel();
	}

	public void setSaturation(float saturation) {
		getFoodData().setSaturation(saturation);
	}

	public FoodData getFoodData() {
		return minecraftPlayer.getFoodData();
	}

	public void addExhaustion(float exhaustion) {
		minecraftPlayer.causeFoodExhaustion(exhaustion);
	}

	public void addXP(int xp) {
		minecraftPlayer.giveExperiencePoints(xp);
	}

	public void addXPLevels(int l) {
		minecraftPlayer.giveExperienceLevels(l);
	}

	public void setXp(int xp) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.giveExperiencePoints(xp);
	}

	public int getXp() {
		return minecraftPlayer.totalExperience;
	}

	public void setXpLevel(int l) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.giveExperienceLevels(l);
	}

	public int getXpLevel() {
		return minecraftPlayer.experienceLevel;
	}

	public abstract void paint(CompoundTag renderer);

	public void boostElytraFlight() {
		if (minecraftPlayer.isFallFlying()) {
			var v = minecraftPlayer.getLookAngle();
			var d0 = 1.5D;
			var d1 = 0.1D;
			var m = minecraftPlayer.getDeltaMovement();
			minecraftPlayer.setDeltaMovement(m.add(v.x * 0.1D + (v.x * 1.5D - m.x) * 0.5D, v.y * 0.1D + (v.y * 1.5D - m.y) * 0.5D, v.z * 0.1D + (v.z * 1.5D - m.z) * 0.5D));
		}
	}

	public void closeInventory() {
		minecraftPlayer.closeContainer();
	}

	public AbstractContainerMenu getOpenInventory() {
		return minecraftPlayer.containerMenu;
	}

	public abstract boolean isMiningBlock();

	public void addItemCooldown(Item item, int ticks) {
		minecraftPlayer.getCooldowns().addCooldown(item, ticks);
	}

	public int getAirSupply() {
		return minecraftPlayer.getAirSupply();
	}

	public void setAirSupply(int air) {
		minecraftPlayer.setAirSupply(air);
	}

	public int getMaxAirSupply() {
		return minecraftPlayer.getMaxAirSupply();
	}

	public Stages getStages() {
		return ((PlayerKJS) minecraftPlayer).getStagesKJS();
	}
}