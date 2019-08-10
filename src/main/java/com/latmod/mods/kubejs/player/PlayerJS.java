package com.latmod.mods.kubejs.player;

import com.latmod.mods.kubejs.entity.EntityJS;
import com.latmod.mods.kubejs.item.InventoryJS;
import com.latmod.mods.kubejs.item.ItemStackJS;
import com.latmod.mods.kubejs.text.TextUtils;
import com.latmod.mods.kubejs.util.UtilsJS;
import com.latmod.mods.kubejs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.stats.StatBase;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * @author LatvianModder
 */
public class PlayerJS extends EntityJS
{
	public final EntityPlayerMP player;
	private InventoryJS inventory;

	public PlayerJS(WorldJS w, EntityPlayerMP p)
	{
		super(w, p);
		player = p;
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		super.setPositionAndRotation(x, y, z, yaw, pitch);
		player.connection.setPlayerLocation(x, y, z, yaw, pitch);
	}

	public void sendStatusMessage(Object... message)
	{
		player.sendStatusMessage(TextUtils.INSTANCE.of(message).component(), true);
	}

	public void give(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player, UtilsJS.INSTANCE.item(item).itemStack(), player.inventory.currentItem);
	}

	public ItemStackJS getEquipment(EntityEquipmentSlot slot)
	{
		return UtilsJS.INSTANCE.item(player.getItemStackFromSlot(slot));
	}

	public void setEquipment(EntityEquipmentSlot slot, Object item)
	{
		player.setItemStackToSlot(slot, UtilsJS.INSTANCE.item(item).itemStack());
	}

	public int selectedSlot()
	{
		return player.inventory.currentItem;
	}

	public InventoryJS inventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryJS(new InvWrapper(player.inventory));
		}

		return inventory;
	}

	public boolean isCreativeMode()
	{
		return player.capabilities.isCreativeMode;
	}

	public boolean isSpectator()
	{
		return player.isSpectator();
	}

	public int getStat(Object id)
	{
		StatBase stat = UtilsJS.INSTANCE.stat(id);
		return stat == null ? 0 : player.getStatFile().readStat(stat);
	}

	public void setStat(Object id, int value)
	{
		StatBase stat = UtilsJS.INSTANCE.stat(id);

		if (stat != null)
		{
			player.getStatFile().unlockAchievement(player, stat, value);
		}
	}

	public void addStat(Object id, int value)
	{
		StatBase stat = UtilsJS.INSTANCE.stat(id);

		if (stat != null)
		{
			player.getStatFile().increaseStat(player, stat, value);
		}
	}
}