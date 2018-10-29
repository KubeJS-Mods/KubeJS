package com.latmod.mods.worldjs.player;

import com.latmod.mods.worldjs.entity.EntityJS;
import com.latmod.mods.worldjs.item.ItemStackJS;
import com.latmod.mods.worldjs.text.TextUtils;
import com.latmod.mods.worldjs.util.UtilsJS;
import com.latmod.mods.worldjs.world.WorldJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class PlayerJS extends EntityJS
{
	public final EntityPlayerMP player;

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

	public void setEquipment(EntityEquipmentSlot slot, ItemStackJS stack)
	{
		player.setItemStackToSlot(slot, stack.itemStack());
	}
}