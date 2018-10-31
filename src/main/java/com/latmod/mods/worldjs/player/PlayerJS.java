package com.latmod.mods.worldjs.player;

import com.latmod.mods.worldjs.entity.EntityJS;
import com.latmod.mods.worldjs.item.InventoryJS;
import com.latmod.mods.worldjs.item.ItemStackJS;
import com.latmod.mods.worldjs.text.TextUtils;
import com.latmod.mods.worldjs.util.UtilsJS;
import com.latmod.mods.worldjs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
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
}